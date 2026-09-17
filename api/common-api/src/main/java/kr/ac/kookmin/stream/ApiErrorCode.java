package kr.ac.kookmin.stream;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import kr.ac.kookmin.stream.common.ErrorCode;

/**
 * API가 실제로 던지는 {@link ErrorCode}를 OpenAPI 응답 예시로 문서화한다.
 * {@code type}으로 enum 클래스를, {@code codes}로 상수 이름을 넘긴다.
 * 한 어노테이션에는 같은 {@code type}만 담고, 다른 enum은 어노테이션을 추가로 붙인다.
 *
 * <p>{@code codes}는 런타임에 enum 상수와 대조해 검증하며, 없는 이름이면 문서 조립 시 즉시 실패한다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ApiErrorCodes.class)
public @interface ApiErrorCode {

    Class<? extends ErrorCode> type();

    String[] codes();
}
