package kr.ac.kookmin.stream.api.app.core.member.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberPhoneNumberUpdateCommand;

public record PhoneNumberUpdateRequest(
    // 가입 때 받은 번호와 형식이 달라지지 않도록 MemberSignUpRequest와 같은 규칙으로 검증한다
    @NotBlank(message = "전화번호를 입력해 주세요.")
    @Pattern(regexp = "^010-?\\d{4}-?\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    String phoneNumber
) {

    public MemberPhoneNumberUpdateCommand toCommand() {
        return new MemberPhoneNumberUpdateCommand(phoneNumber);
    }
}
