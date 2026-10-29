package kr.ac.kookmin.stream.common;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 락 충돌을 다루며 작업을 실행한다.
 */
@Component
public class LockExecutor {

    private static final int MAX_ATTEMPTS = 3;
    private static final long MIN_BACKOFF_MILLIS = 30;
    private static final long MAX_BACKOFF_MILLIS = 100;

    private final TransactionTemplate transactionTemplate;

    // 공유 TransactionTemplate 빈 대신 전용 템플릿을 만든다. 다른 곳에서 템플릿 설정(타임아웃 등)을 바꿔도 영향받지 않게 한다
    public LockExecutor(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * 낙관적 락이 충돌하면 재시도하며 action을 실행한다.
     * <p>
     * 시도마다 새 트랜잭션을 열고 action 전체를 그 안에서 실행한 뒤 커밋한다. 낙관적 락이 충돌하면
     * ({@link OptimisticLockingFailureException}) 롤백하고 잠깐 쉰 뒤, 새 트랜잭션에서 action을 처음부터 다시 실행한다.
     * 같은 트랜잭션 안에서 다시 시도하면 1차 캐시와 REPEATABLE READ 스냅샷이 예전 값을 계속 돌려줘 매번 충돌하므로,
     * 트랜잭션은 반드시 시도마다 새로 연다. 커밋 시점에 터지는 충돌도 이 안에서 잡힌다.
     * <ul>
     *   <li>이미 트랜잭션이 있는 곳에서 호출하면 그 트랜잭션에 참여해 재시도가 효과가 없으므로 {@link IllegalStateException}을 던진다.</li>
     *   <li>action은 충돌할 때마다 처음부터 다시 실행된다. 롤백되지 않는 부수효과(외부 API 호출 등)를 넣지 않는다.</li>
     *   <li>시도를 다 써도 충돌하면 {@link CommonErrorCode#OPTIMISTIC_LOCK_CONFLICT}로 실패한다.
     *       action이 던진 다른 예외는 재시도하지 않고 그대로 던진다.</li>
     * </ul>
     */
    public <T> T executeOptimistic(Supplier<T> action) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalStateException("executeOptimistic()은 트랜잭션 밖에서 호출해야 합니다.");
        }

        for (int attempt = 1; ; attempt++) {
            try {
                return transactionTemplate.execute(status -> action.get());
            } catch (OptimisticLockingFailureException e) {
                if (attempt == MAX_ATTEMPTS) {
                    throw new BusinessException(CommonErrorCode.OPTIMISTIC_LOCK_CONFLICT);
                }
                waitBeforeRetry();
            }
        }
    }

    /** 반환값이 없는 action용. 동작은 {@link #executeOptimistic(Supplier)}와 같다. */
    public void executeOptimistic(Runnable action) {
        executeOptimistic(() -> {
            action.run();
            return null;
        });
    }

    // 함께 충돌한 요청들이 같은 간격으로 다시 부딪히지 않게 대기 시간을 무작위로 둔다
    private void waitBeforeRetry() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(MIN_BACKOFF_MILLIS, MAX_BACKOFF_MILLIS + 1));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(CommonErrorCode.OPTIMISTIC_LOCK_CONFLICT);
        }
    }
}
