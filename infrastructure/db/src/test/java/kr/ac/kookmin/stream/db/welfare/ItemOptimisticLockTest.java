package kr.ac.kookmin.stream.db.welfare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;
import kr.ac.kookmin.stream.common.LockExecutor;
import kr.ac.kookmin.stream.db.MySqlJpaTest;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemType;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 물품 재고의 버전 컬럼 없는 낙관적 락(ItemJpaEntity의 DIRTY)이 실제 MySQL에서 동작하는지, 그리고 LockExecutor가
 * 그 충돌을 잡아 새 트랜잭션에서 다시 시도하는지 확인한다.
 * <p>
 * 동시에 들어온 다른 요청은 스레드 대신 REQUIRES_NEW 트랜잭션으로 흉내 낸다. 바깥 트랜잭션이 재고를 읽은 뒤
 * 별도 트랜잭션(별도 커넥션)이 먼저 커밋하게 해서, 실행 순서를 고정하고 결과가 매번 같게 한다.
 * 실제 커밋이 일어나야 하므로 테스트 메서드를 트랜잭션으로 감싸지 않는다(NOT_SUPPORTED).
 */
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({ItemRepositoryImpl.class, LockExecutor.class})
class ItemOptimisticLockTest extends MySqlJpaTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private LockExecutor lockExecutor;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    @DisplayName("다른 트랜잭션이 같은 물품의 재고를 먼저 바꾸고 커밋하면, 늦게 커밋하는 쪽이 충돌로 실패한다")
    void laterCommitFailsWhenStockChangedConcurrently() {
        Long itemId = createItem(5);

        assertThrows(OptimisticLockingFailureException.class, () -> inTransaction(() -> {
            Item item = itemRepository.findById(itemId).orElseThrow();     // 재고 5를 읽는다
            inOtherTransaction(() -> decreaseStock(itemId, 2));            // 다른 요청이 먼저 3으로 커밋한다
            item.decreaseStock(1);
            itemRepository.save(item);                                     // 커밋할 때 WHERE count = 5 → 0건
        }));

        assertEquals(3, countOf(itemId));
    }

    @Test
    @DisplayName("운영진이 SQL로 재고를 직접 바꿔도 충돌로 감지한다")
    void detectsDirectSqlStockChange() {
        Long itemId = createItem(5);

        assertThrows(OptimisticLockingFailureException.class, () -> inTransaction(() -> {
            Item item = itemRepository.findById(itemId).orElseThrow();
            inOtherTransaction(() -> jdbcTemplate.update("UPDATE items SET count = ? WHERE id = ?", 10, itemId));
            item.decreaseStock(1);
            itemRepository.save(item);
        }));

        assertEquals(10, countOf(itemId));
    }

    @Test
    @DisplayName("서로 다른 컬럼을 고친 동시 수정은 충돌하지 않고 둘 다 반영된다")
    void concurrentChangesOnDifferentColumnsBothApply() {
        Long itemId = createItem(5);

        inTransaction(() -> {
            Item item = itemRepository.findById(itemId).orElseThrow();
            inOtherTransaction(() -> jdbcTemplate.update("UPDATE items SET name = ? WHERE id = ?", "장우산", itemId));
            item.decreaseStock(1);
            itemRepository.save(item);                                     // 바뀐 count만 UPDATE한다(@DynamicUpdate)
        });

        assertEquals(4, countOf(itemId));
        assertEquals("장우산", nameOf(itemId));
    }

    @Test
    @DisplayName("LockExecutor로 감싸면 충돌한 시도를 새 트랜잭션에서 다시 읽어 성공한다")
    void lockExecutorRetriesWithFreshRead() {
        Long itemId = createItem(5);
        AtomicInteger attempts = new AtomicInteger();

        lockExecutor.executeOptimistic(() -> decreaseOneWithConcurrentChangeOnFirstAttempt(itemId, attempts));

        assertEquals(2, attempts.get());
        assertEquals(2, countOf(itemId));                                  // 5 - 2(끼어든 차감) - 1
    }

    @Test
    @DisplayName("OSIV처럼 EntityManager가 스레드에 묶여 있어도, 재시도는 DB에서 새로 읽어 성공한다")
    void lockExecutorRetriesWithPreBoundEntityManager() {
        Long itemId = createItem(5);
        AtomicInteger attempts = new AtomicInteger();

        // OpenEntityManagerInViewInterceptor가 요청을 시작할 때 하는 일을 그대로 흉내 낸다
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(entityManager));
        try {
            lockExecutor.executeOptimistic(() -> decreaseOneWithConcurrentChangeOnFirstAttempt(itemId, attempts));
        } finally {
            TransactionSynchronizationManager.unbindResource(entityManagerFactory);
            entityManager.close();
        }

        assertEquals(2, attempts.get());
        assertEquals(2, countOf(itemId));
    }

    // 재고를 읽은 뒤, 첫 시도에서만 다른 요청이 끼어들어 2개를 먼저 차감한다. 그 위에서 1개를 차감한다
    private void decreaseOneWithConcurrentChangeOnFirstAttempt(Long itemId, AtomicInteger attempts) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        if (attempts.incrementAndGet() == 1) {
            inOtherTransaction(() -> decreaseStock(itemId, 2));
        }
        item.decreaseStock(1);
        itemRepository.save(item);
    }

    // 대여 신청(ItemServiceImpl.decreaseStock)과 같은 방식(읽기 → 도메인에서 차감 → 저장)으로 재고를 뺀다
    private void decreaseStock(Long itemId, int amount) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        item.decreaseStock(amount);
        itemRepository.save(item);
    }

    private void inTransaction(Runnable work) {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> work.run());
    }

    // 바깥 트랜잭션을 잠시 멈추고 별도 트랜잭션(별도 커넥션)에서 실행해 바로 커밋한다
    private void inOtherTransaction(Runnable work) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        template.executeWithoutResult(status -> work.run());
    }

    private Long createItem(int count) {
        Item item = Item.of(null, "우산", ItemCategory.DAILY_SUPPLIES, ItemType.CONSUMABLE, count, null, null);
        return itemRepository.save(item).getId();
    }

    private int countOf(Long itemId) {
        return jdbcTemplate.queryForObject("SELECT count FROM items WHERE id = ?", Integer.class, itemId);
    }

    private String nameOf(Long itemId) {
        return jdbcTemplate.queryForObject("SELECT name FROM items WHERE id = ?", String.class, itemId);
    }
}
