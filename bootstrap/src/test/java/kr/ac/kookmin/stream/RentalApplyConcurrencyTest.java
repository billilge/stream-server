package kr.ac.kookmin.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import kr.ac.kookmin.stream.api.app.welfare.rental.usecase.RentalApplyUseCase;
import kr.ac.kookmin.stream.common.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * 대여 신청이 실제로 동시에 몰릴 때 재고와 대여 이력이 어긋나지 않는지, 애플리케이션 전체를 띄워 확인한다.
 * <p>
 * 몇 건이 성공할지는 스레드 타이밍(재시도 3회를 다 쓰고 409가 나는지 등)에 따라 달라서 성공 건수 자체는 고정하지 않고,
 * 타이밍과 관계없이 항상 성립해야 하는 조건만 검사한다. 재고가 음수가 되지 않는지, 차감된 재고와 이력 수가 맞는지,
 * 예상하지 못한 예외(500으로 나갈 예외)가 없는지다.
 */
class RentalApplyConcurrencyTest extends MySqlIntegrationTest {

    private static final String SUCCESS = "SUCCESS";
    private static final int RENT_AT_HOUR = 11;
    private static final int RENT_AT_MINUTE = 0;

    @Autowired
    private RentalApplyUseCase rentalApplyUseCase;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    @DisplayName("재고보다 많은 신청이 동시에 몰려도 재고가 음수가 되지 않고, 차감된 만큼만 대여 이력이 생긴다")
    void concurrentApplicationsNeverOversell() throws InterruptedException {
        long memberId = registerPayer(1001L);
        long itemId = createItem("CONSUMABLE", 5);

        Map<String, Integer> outcomes = applyConcurrently(20, () ->
            rentalApplyUseCase.apply(memberId, itemId, 1, RENT_AT_HOUR, RENT_AT_MINUTE, true)
        );

        assertOnlyExpectedOutcomes(outcomes, SUCCESS, "ITEM_OUT_OF_STOCK", "OPTIMISTIC_LOCK_CONFLICT");
        assertEquals(20, total(outcomes));
        int successes = outcomes.getOrDefault(SUCCESS, 0);
        // 먼저 커밋하는 신청은 항상 성공하므로 적어도 1건은 성공한다
        assertTrue(successes >= 1 && successes <= 5, () -> "성공 건수가 1~5 밖이다: " + outcomes);
        assertEquals(5 - successes, countOf(itemId));
        assertEquals(successes, historyCountOf(itemId));
    }

    @Test
    @DisplayName("같은 회원이 같은 대여품을 동시에 여러 번 신청해도 대여 중 이력은 하나만 생긴다")
    void concurrentDuplicateApplicationsCreateOneRental() throws InterruptedException {
        long memberId = registerPayer(1002L);
        long itemId = createItem("RENTAL", 10);

        Map<String, Integer> outcomes = applyConcurrently(5, () ->
            rentalApplyUseCase.apply(memberId, itemId, 1, RENT_AT_HOUR, RENT_AT_MINUTE, false)
        );

        assertOnlyExpectedOutcomes(outcomes, SUCCESS, "RENTAL_ITEM_DUPLICATED", "OPTIMISTIC_LOCK_CONFLICT");
        // 늦게 커밋한 쪽은 재고 충돌로 다시 시도하고, 새 스냅샷에서 먼저 커밋된 이력을 보고 중복으로 거절된다
        assertEquals(1, outcomes.getOrDefault(SUCCESS, 0), () -> "성공이 정확히 1건이 아니다: " + outcomes);
        assertEquals(1, activeRentalCountOf(itemId, memberId));
        assertEquals(9, countOf(itemId));
    }

    // 모든 스레드가 출발선에 선 뒤 한꺼번에 신청을 시작하게 하고, 결과를 성공 또는 에러 코드(그 밖의 예외는 클래스 이름)별로 센다
    private Map<String, Integer> applyConcurrently(int requests, Runnable apply) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(requests);
        CountDownLatch ready = new CountDownLatch(requests);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(requests);
        Map<String, Integer> outcomes = new ConcurrentHashMap<>();

        for (int i = 0; i < requests; i++) {
            executor.execute(() -> {
                ready.countDown();
                try {
                    start.await();
                    apply.run();
                    outcomes.merge(SUCCESS, 1, Integer::sum);
                } catch (BusinessException e) {
                    outcomes.merge(e.getErrorCode().name(), 1, Integer::sum);
                } catch (Exception e) {
                    outcomes.merge(e.getClass().getSimpleName(), 1, Integer::sum);
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        start.countDown();
        boolean finished = done.await(1, TimeUnit.MINUTES);
        executor.shutdownNow();
        assertTrue(finished, "1분 안에 모든 신청이 끝나지 않았다");
        return outcomes;
    }

    private void assertOnlyExpectedOutcomes(Map<String, Integer> outcomes, String... expected) {
        assertTrue(Set.of(expected).containsAll(outcomes.keySet()), () -> "예상하지 못한 결과가 있다: " + outcomes);
    }

    private int total(Map<String, Integer> outcomes) {
        return outcomes.values().stream().mapToInt(Integer::intValue).sum();
    }

    private long registerPayer(long memberId) {
        jdbcTemplate.update(
            "INSERT INTO payers (member_id, name, enrollment_year, registered) VALUES (?, ?, ?, ?)",
            memberId, "학생" + memberId, "2026", true
        );
        return memberId;
    }

    private long createItem(String type, int count) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO items (name, category, type, count) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, "물품");
            statement.setString(2, "DAILY_SUPPLIES");
            statement.setString(3, type);
            statement.setInt(4, count);
            return statement;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    private int countOf(long itemId) {
        return jdbcTemplate.queryForObject("SELECT count FROM items WHERE id = ?", Integer.class, itemId);
    }

    private int historyCountOf(long itemId) {
        return jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM rental_histories WHERE item_id = ?", Integer.class, itemId
        );
    }

    private int activeRentalCountOf(long itemId, long memberId) {
        return jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM rental_histories WHERE item_id = ? AND member_id = ? AND rental_status = 'RENTAL'",
            Integer.class, itemId, memberId
        );
    }
}
