package kr.ac.kookmin.stream.api.common.openapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link ApiErrorCode}의 반복 적용을 담는 컨테이너 어노테이션.
 * Java {@code @Repeatable}은 컨테이너 어노테이션을 명시해야 한다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorCodes {

    ApiErrorCode[] value();
}
