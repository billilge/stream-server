package kr.ac.kookmin.stream.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 낙관적 락 충돌만 재시도하고, 시도마다 트랜잭션을 새로 여는지 확인한다.
 * 트랜잭션 매니저는 시작·커밋·롤백 횟수만 세는 가짜로 바꿔 DB 없이 검증한다.
 */
class LockExecutorTest {

    private final CountingTransactionManager transactionManager = new CountingTransactionManager();
    private final LockExecutor lockExecutor = new LockExecutor(transactionManager);
    private final AtomicInteger calls = new AtomicInteger();

    @AfterEach
    void clearThreadState() {
        // 테스트가 바꾼 스레드 상태가 다음 테스트로 새지 않게 되돌린다
        TransactionSynchronizationManager.setActualTransactionActive(false);
        Thread.interrupted();
    }

    @Test
    @DisplayName("충돌이 없으면 한 번 실행하고 결과를 그대로 돌려준다")
    void returnsResultWithoutRetry() {
        String result = lockExecutor.executeOptimistic(() -> {
            calls.incrementAndGet();
            return "완료";
        });

        assertEquals("완료", result);
        assertEquals(1, calls.get());
        assertEquals(1, transactionManager.begun);
        assertEquals(1, transactionManager.committed);
        assertEquals(0, transactionManager.rolledBack);
    }

    @Test
    @DisplayName("반환값이 없는 action도 같은 방식으로 실행한다")
    void runsRunnableAction() {
        lockExecutor.executeOptimistic(() -> {
            calls.incrementAndGet();
        });

        assertEquals(1, calls.get());
        assertEquals(1, transactionManager.committed);
    }

    @Test
    @DisplayName("낙관적 락이 충돌하면 롤백하고 새 트랜잭션에서 다시 실행한다")
    void retriesInNewTransactionAfterConflict() {
        String result = lockExecutor.executeOptimistic(() -> {
            if (calls.incrementAndGet() == 1) {
                throw new OptimisticLockingFailureException("재고 충돌");
            }
            return "완료";
        });

        assertEquals("완료", result);
        assertEquals(2, calls.get());
        assertEquals(2, transactionManager.begun);
        assertEquals(1, transactionManager.rolledBack);
        assertEquals(1, transactionManager.committed);
    }

    @Test
    @DisplayName("세 번 모두 충돌하면 OPTIMISTIC_LOCK_CONFLICT로 실패한다")
    void failsWithConflictAfterMaxAttempts() {
        BusinessException exception = assertThrows(BusinessException.class, () ->
            lockExecutor.executeOptimistic(() -> {
                calls.incrementAndGet();
                throw new OptimisticLockingFailureException("재고 충돌");
            })
        );

        assertEquals(CommonErrorCode.OPTIMISTIC_LOCK_CONFLICT, exception.getErrorCode());
        assertEquals(3, calls.get());
        assertEquals(3, transactionManager.rolledBack);
        assertEquals(0, transactionManager.committed);
    }

    @Test
    @DisplayName("낙관적 락 충돌이 아닌 예외는 재시도하지 않고 그대로 던진다")
    void doesNotRetryOtherExceptions() {
        BusinessException thrown = new BusinessException(CommonErrorCode.INVALID_INPUT);

        BusinessException exception = assertThrows(BusinessException.class, () ->
            lockExecutor.executeOptimistic(() -> {
                calls.incrementAndGet();
                throw thrown;
            })
        );

        assertSame(thrown, exception);
        assertEquals(1, calls.get());
        assertEquals(1, transactionManager.rolledBack);
    }

    @Test
    @DisplayName("이미 트랜잭션 안이면 재시도가 소용없으므로 실행하지 않고 실패한다")
    void rejectsCallInsideTransaction() {
        TransactionSynchronizationManager.setActualTransactionActive(true);

        assertThrows(IllegalStateException.class, () ->
            lockExecutor.executeOptimistic(() -> {
                calls.incrementAndGet();
            })
        );

        assertEquals(0, calls.get());
        assertEquals(0, transactionManager.begun);
    }

    @Test
    @DisplayName("재시도를 기다리다 인터럽트되면 인터럽트 상태를 남기고 OPTIMISTIC_LOCK_CONFLICT로 실패한다")
    void keepsInterruptFlagWhenInterruptedWhileWaiting() {
        BusinessException exception = assertThrows(BusinessException.class, () ->
            lockExecutor.executeOptimistic(() -> {
                calls.incrementAndGet();
                // 인터럽트 상태에서 대기에 들어가면 sleep이 곧바로 InterruptedException을 던진다
                Thread.currentThread().interrupt();
                throw new OptimisticLockingFailureException("재고 충돌");
            })
        );

        assertEquals(CommonErrorCode.OPTIMISTIC_LOCK_CONFLICT, exception.getErrorCode());
        assertEquals(1, calls.get());
        assertTrue(Thread.currentThread().isInterrupted());
    }

    private static final class CountingTransactionManager implements PlatformTransactionManager {

        private int begun;
        private int committed;
        private int rolledBack;

        @Override
        public TransactionStatus getTransaction(TransactionDefinition definition) {
            begun++;
            return new SimpleTransactionStatus();
        }

        @Override
        public void commit(TransactionStatus status) {
            committed++;
        }

        @Override
        public void rollback(TransactionStatus status) {
            rolledBack++;
        }
    }
}
