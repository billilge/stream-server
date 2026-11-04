package kr.ac.kookmin.stream.api.admin.internal.schedule.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkScheduleCommand;

// 수정 모달이 근무자·근무일·시간을 한꺼번에 보내므로 일부가 아니라 전체를 바꾼다
public record WorkScheduleUpdateRequest(

    @Schema(description = "근무자 회원 아이디. 다른 근무자로 바꿀 수 있다", example = "15")
    @NotNull(message = "근무자를 선택해 주세요.")
    Long memberId,

    @Schema(description = "근무일(월~금)", example = "2026-10-21")
    @NotNull(message = "근무일을 입력해 주세요.")
    LocalDate workDate,

    @Schema(description = "시작 시각(시)", example = "13")
    @NotNull(message = "시작 시각을 입력해 주세요.")
    Integer startHour,

    @Schema(description = "종료 시각(시)", example = "15")
    @NotNull(message = "종료 시각을 입력해 주세요.")
    Integer endHour
) {

    public WorkScheduleCommand toCommand() {
        return new WorkScheduleCommand(workDate, startHour, endHour);
    }
}
