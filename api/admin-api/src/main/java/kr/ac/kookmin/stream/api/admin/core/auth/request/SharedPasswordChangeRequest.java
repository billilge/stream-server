package kr.ac.kookmin.stream.api.admin.core.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SharedPasswordChangeRequest(
    @Schema(description = "현재 공용 비밀번호")
    @NotBlank(message = "현재 비밀번호를 입력해 주세요.")
    String currentPassword,

    // 공백 없는 ASCII(영문 대소문자·숫자·기호)만 받는다. 16자 이하 ASCII라 BCrypt의 72바이트 제한에 걸리지 않는다
    @Schema(description = "새 공용 비밀번호 (8~16자, 영문·숫자·기호)")
    @NotBlank(message = "새 비밀번호를 입력해 주세요.")
    @Size(min = 8, max = 16, message = "새 비밀번호는 8~16자로 입력해 주세요.")
    @Pattern(regexp = "^[\\x21-\\x7E]+$", message = "새 비밀번호는 영문·숫자·기호만 쓸 수 있습니다.")
    String newPassword
) {
}
