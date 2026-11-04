package kr.ac.kookmin.stream.member.domain.member.domain;

import java.util.Locale;
import java.util.Map;

/**
 * 회원가입 요청. 전화번호는 하이픈을 지운 숫자만, 이메일은 소문자로 바꿔 담는다. 중복 검사·인증 확인과 저장이 같은 형식을 쓰게 하기 위함이다.
 */
public record MemberSignUpCommand(
    String phoneNumber,
    String email,
    Map<TermType, Boolean> termAgreements
) {

    public MemberSignUpCommand {
        phoneNumber = phoneNumber.replace("-", "");
        email = email.toLowerCase(Locale.ROOT);
    }
}
