package kr.ac.kookmin.stream.common;

import java.time.Duration;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.stereotype.Component;

/**
 * 실패한 작업을 다시 실행한다.
 */
@Component
public class RetryExecutor {

    /**
     * retryOn에 맞는 예외가 나면 delay만큼 쉬고 action을 다시 실행한다. 첫 시도 뒤 최대 maxRetries번 더 실행한다.
     * <ul>
     *   <li>트랜잭션을 열지 않는다. 시도마다 action을 처음부터 다시 실행하므로 중복돼도 되는 작업만 넣는다.</li>
     *   <li>retryOn에 맞지 않는 예외는 바로 던진다. 시도를 다 쓰면 마지막 예외를 그대로 던진다.</li>
     *   <li>대기는 호출한 스레드를 멈춘다. 대기 중 인터럽트되면 인터럽트 상태를 되살리고 마지막 예외를 던진다.</li>
     * </ul>
     */
    public <T> T executeWithRetry(
        Supplier<T> action,
        Predicate<Throwable> retryOn,
        int maxRetries,
        Duration delay
    ) {
        RetryPolicy retryPolicy = RetryPolicy.builder()
            .predicate(retryOn)
            .maxRetries(maxRetries)
            .delay(delay)
            .build();
        return new RetryTemplate(retryPolicy).invoke(action);
    }

    /** retryOn 타입(하위 타입 포함)의 예외만 재시도한다. 동작은 {@link #executeWithRetry(Supplier, Predicate, int, Duration)}와 같다. */
    public <T> T executeWithRetry(
        Supplier<T> action,
        Class<? extends RuntimeException> retryOn,
        int maxRetries,
        Duration delay
    ) {
        return executeWithRetry(action, retryOn::isInstance, maxRetries, delay);
    }

    /** 반환값이 없는 action용. 동작은 {@link #executeWithRetry(Supplier, Predicate, int, Duration)}와 같다. */
    public void executeWithRetry(
        Runnable action,
        Predicate<Throwable> retryOn,
        int maxRetries,
        Duration delay
    ) {
        executeWithRetry(() -> {
            action.run();
            return null;
        }, retryOn, maxRetries, delay);
    }

    /** 반환값이 없는 action용. 동작은 {@link #executeWithRetry(Supplier, Class, int, Duration)}와 같다. */
    public void executeWithRetry(
        Runnable action,
        Class<? extends RuntimeException> retryOn,
        int maxRetries,
        Duration delay
    ) {
        executeWithRetry(action, retryOn::isInstance, maxRetries, delay);
    }
}
