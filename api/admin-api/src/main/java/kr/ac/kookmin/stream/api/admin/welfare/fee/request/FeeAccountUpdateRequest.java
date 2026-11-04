package kr.ac.kookmin.stream.api.admin.welfare.fee.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/**
 * 회비 납부 설정 변경. 계좌와 학기당 회비를 한 화면에서 같이 저장하므로 함께 받는다.
 */
public record FeeAccountUpdateRequest(
    @Schema(description = "은행명 (허용 목록 중 하나)", example = "카카오뱅크")
    @NotBlank(message = "은행을 선택해 주세요.")
    String bank,

    @Schema(description = "계좌번호 (하이픈 제외, 10~14자리)", example = "3333011234567")
    @NotBlank(message = "계좌번호를 입력해 주세요.")
    @Pattern(regexp = "^\\d{10,14}$", message = "계좌번호는 하이픈 없이 숫자 10~14자리로 입력해 주세요.")
    String accountNo,

    @Schema(description = "학기당 학생회비", example = "20000")
    @NotNull(message = "학기당 학생회비를 입력해 주세요.")
    @Positive(message = "학기당 학생회비는 1원 이상이어야 합니다.")
    Integer amount
) {
}
