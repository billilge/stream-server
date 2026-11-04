package kr.ac.kookmin.stream.api.admin.event.locker.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodCreateCommand;

/**
 * 회차 이름은 연도와 학기로 서버가 만든다. 게시 여부도 받지 않는다 — 학생에게 회차를 노출하는 경로는 홈 화면 배너뿐이고
 * 그쪽에서 따로 설정한다.
 * <p>
 * 신청 기간은 날짜만 받고 시작일 00:00:00 ~ 마감일 23:59:59로 저장한다.
 */
public record LockerPeriodCreateRequest(
    @NotNull(message = "연도를 입력해 주세요.")
    @Min(value = 2000, message = "연도가 올바르지 않습니다.")
    @Max(value = 2100, message = "연도가 올바르지 않습니다.")
    Integer year,

    @NotNull(message = "학기를 입력해 주세요.")
    @Min(value = 1, message = "학기는 1 또는 2여야 합니다.")
    @Max(value = 2, message = "학기는 1 또는 2여야 합니다.")
    Integer semester,

    @NotNull(message = "신청 시작일을 입력해 주세요.")
    LocalDate applyStartDate,

    @NotNull(message = "신청 마감일을 입력해 주세요.")
    LocalDate applyEndDate,

    @NotNull(message = "사용 시작일을 입력해 주세요.")
    LocalDate usageStartDate,

    @NotNull(message = "사용 종료일을 입력해 주세요.")
    LocalDate usageEndDate
) {

    public LockerPeriodCreateCommand toCommand() {
        return new LockerPeriodCreateCommand(
            year, semester, applyStartDate, applyEndDate, usageStartDate, usageEndDate);
    }
}
