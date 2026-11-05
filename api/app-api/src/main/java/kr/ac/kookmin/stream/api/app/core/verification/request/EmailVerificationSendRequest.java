package kr.ac.kookmin.stream.api.app.core.verification.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kr.ac.kookmin.stream.member.domain.verification.domain.EmailVerificationSendCommand;

public record EmailVerificationSendRequest(
    @NotBlank(message = "이메일을 입력해 주세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 255, message = "이메일은 255자 이하로 입력해 주세요.")
    String email
) {

    public EmailVerificationSendCommand toCommand() {
        return new EmailVerificationSendCommand(email);
    }
}
