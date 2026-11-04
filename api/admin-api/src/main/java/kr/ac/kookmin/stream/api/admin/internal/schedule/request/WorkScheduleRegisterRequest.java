package kr.ac.kookmin.stream.api.admin.internal.schedule.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkScheduleCommand;

/**
 * 근무 시간 등록 요청. 화면이 근무자 한 명을 고르고 시간대를 여러 개 넣어 한 번에 저장한다.
 * 근무일·시간의 범위(월~금, 10~17시, 점심시간 제외)는 도메인이 검증한다.
 */
public record WorkScheduleRegisterRequest(

    @Schema(description = "근무자 회원 아이디 (근무자 목록 조회의 memberId)", example = "11")
    @NotNull(message = "근무자를 선택해 주세요.")
    Long memberId,

    @Schema(description = "등록할 시간대 목록. 최소 1건")
    @NotEmpty(message = "시간대를 1개 이상 입력해 주세요.")
    List<@Valid @NotNull(message = "시간대를 입력해 주세요.") Item> schedules
) {

    public List<WorkScheduleCommand> toCommands() {
        return schedules.stream()
            .map(item -> new WorkScheduleCommand(item.workDate(), item.startHour(), item.endHour()))
            .toList();
    }

    public record Item(

        @Schema(description = "근무일(월~금)", example = "2026-10-20")
        @NotNull(message = "근무일을 입력해 주세요.")
        LocalDate workDate,

        @Schema(description = "시작 시각(시)", example = "10")
        @NotNull(message = "시작 시각을 입력해 주세요.")
        Integer startHour,

        @Schema(description = "종료 시각(시)", example = "12")
        @NotNull(message = "종료 시각을 입력해 주세요.")
        Integer endHour
    ) {
    }
}
