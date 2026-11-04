package kr.ac.kookmin.stream.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * {@code @Async}에 적는 실행기 빈 이름. 실행기는 bootstrap의 AsyncConfig가 등록한다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TaskExecutorNames {

    public static final String EMAIL_TASK_EXECUTOR = "emailTaskExecutor";
}
