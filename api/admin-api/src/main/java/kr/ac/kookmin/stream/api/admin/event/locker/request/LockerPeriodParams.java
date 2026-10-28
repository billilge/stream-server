package kr.ac.kookmin.stream.api.admin.event.locker.request;

import jakarta.validation.constraints.NotNull;

/**
 * 운영진 조회의 기준 회차. 구역 조회와 신청 현황 조회가 함께 쓴다. 구역은 운영 회차에 묶여 있지 않고,
 * 회차는 "어느 회차의 배정 현황을 볼지"를 정한다.
 * <p>
 * 필드가 하나뿐이지만 {@code @RequestParam}으로 받지 않는다. {@code GlobalExceptionHandler}가
 * {@code MissingServletRequestParameterException}을 다루지 않아 값이 빠지면 500이 되고,
 * {@code @Valid @ModelAttribute}여야 400 {@code INVALID_INPUT}으로 나간다.
 *
 * @param lockerPeriodId 조회할 사물함 운영 회차 식별자
 */
public record LockerPeriodParams(

    @NotNull(message = "사물함 운영 회차를 입력해 주세요.")
    Long lockerPeriodId
) {
}
