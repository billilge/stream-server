package kr.ac.kookmin.stream.api.app.welfare.fee.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 학생회비 납부 요청. 송금을 마친 뒤 남은 학기 수와 그 근거가 되는 학점이수현황 캡처를 함께 보낸다.
 */
public record FeeSubmitRequest(
    @Schema(description = "남은 학기 수 (1~8)", example = "6")
    @NotNull(message = "남은 학기 수를 입력해 주세요.")
    @Min(value = 1, message = "남은 학기 수는 1 이상 8 이하여야 합니다.")
    @Max(value = 8, message = "남은 학기 수는 1 이상 8 이하여야 합니다.")
    Integer remainingSemesters,

    @Schema(description = "업로드 URL 발급 때 받은 이미지 키", example = "files/3f2a9c1e-7b4d-4e8a-9c2f-1a2b3c4d5e6f.png")
    @NotBlank(message = "학점이수현황 캡처를 첨부해 주세요.")
    String proofImageKey
) {
}
