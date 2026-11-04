package kr.ac.kookmin.stream.api.admin.event.locker.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodUpdateCommand;

/**
 * 전체 교체다. 바꾸지 않는 값도 원래 값 그대로 보낸다. 연도·학기와 게시 여부는 이 경로로 바뀌지 않는다.
 * <p>
 * 진행 중인 회차는 신청 시작일을 원래 값 그대로 보내야 한다.
 */
public record LockerPeriodUpdateRequest(
    @NotNull(message = "신청 시작일을 입력해 주세요.")
    LocalDate applyStartDate,

    @NotNull(message = "신청 마감일을 입력해 주세요.")
    LocalDate applyEndDate,

    @NotNull(message = "사용 시작일을 입력해 주세요.")
    LocalDate usageStartDate,

    @NotNull(message = "사용 종료일을 입력해 주세요.")
    LocalDate usageEndDate
) {

    public LockerPeriodUpdateCommand toCommand() {
        return new LockerPeriodUpdateCommand(applyStartDate, applyEndDate, usageStartDate, usageEndDate);
    }
}
