package kr.ac.kookmin.stream.bootstrap.config;

import java.util.Map;
import kr.ac.kookmin.stream.common.TaskExecutorNames;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * {@code @Async}를 켜고 작업별 실행기를 등록한다. 빈 이름은 {@link TaskExecutorNames}에 둔다.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    private static final int EMAIL_POOL_SIZE = 2;
    private static final int EMAIL_QUEUE_CAPACITY = 100;
    private static final int EMAIL_AWAIT_TERMINATION_SECONDS = 10;

    @Bean(TaskExecutorNames.EMAIL_TASK_EXECUTOR)
    public ThreadPoolTaskExecutor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("email-");
        executor.setCorePoolSize(EMAIL_POOL_SIZE);
        executor.setMaxPoolSize(EMAIL_POOL_SIZE);
        executor.setQueueCapacity(EMAIL_QUEUE_CAPACITY);
        executor.setTaskDecorator(AsyncConfig::copyMdc);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(EMAIL_AWAIT_TERMINATION_SECONDS);
        return executor;
    }

    private static Runnable copyMdc(Runnable task) {
        Map<String, String> context = MDC.getCopyOfContextMap();
        return () -> {
            if (context != null) {
                MDC.setContextMap(context);
            }
            try {
                task.run();
            } finally {
                MDC.clear();
            }
        };
    }
}
