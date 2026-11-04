package kr.ac.kookmin.stream.api.admin.core.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AdminLoginRequest(
    @Schema(description = "학번", example = "20231234")
    @NotBlank(message = "학번을 입력해 주세요.")
    String studentId,

    @Schema(description = "운영진 공용 비밀번호")
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    String password
) {
}
