package kr.ac.kookmin.stream.api.admin.welfare.feedback.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

// 화면이 입력받는 값은 질문 접수 기간 하나뿐이다. 연도·회차 번호는 서버가 배정한다
public record FeedbackRoundCreateRequest(

    @Schema(description = "질문 접수 시작일", example = "2026-10-16")
    @NotNull(message = "질문 접수 시작일과 마감일을 모두 입력해 주세요.")
    LocalDate opensOn,

    @Schema(description = "질문 접수 마감일", example = "2026-10-24")
    @NotNull(message = "질문 접수 시작일과 마감일을 모두 입력해 주세요.")
    LocalDate closesOn
) {
}
