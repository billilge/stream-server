package kr.ac.kookmin.stream.api.app.event.locker.request;

import jakarta.validation.constraints.NotNull;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplyCommand;

/**
 * @param lockerPeriodId 신청할 운영 회차. 구역 조회에 쓴 회차 식별자를 그대로 보낸다
 */
public record LockerApplyRequest(
    @NotNull(message = "사물함 운영 회차를 입력해 주세요.")
    Long lockerPeriodId,

    @NotNull(message = "신청할 사물함을 선택해 주세요.")
    Long lockerId
) {

    public LockerApplyCommand toCommand() {
        return new LockerApplyCommand(lockerPeriodId, lockerId);
    }
}
