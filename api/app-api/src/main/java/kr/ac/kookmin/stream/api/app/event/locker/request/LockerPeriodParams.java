package kr.ac.kookmin.stream.api.app.event.locker.request;

import jakarta.validation.constraints.NotNull;

/**
 * @param lockerPeriodId 조회할 사물함 운영 회차 식별자
 */
public record LockerPeriodParams(

    @NotNull(message = "사물함 운영 회차를 입력해 주세요.")
    Long lockerPeriodId
) {
}
