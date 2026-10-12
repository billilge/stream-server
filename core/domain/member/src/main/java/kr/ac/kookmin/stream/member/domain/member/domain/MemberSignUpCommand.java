package kr.ac.kookmin.stream.member.domain.member.domain;

import java.util.Map;

/**
 * 회원가입 요청. 전화번호는 하이픈을 지운 숫자만 담는다. 중복 검사와 저장이 같은 형식을 쓰게 하기 위함이다.
 */
public record MemberSignUpCommand(
    String phoneNumber,
    Map<TermType, Boolean> termAgreements
) {

    public MemberSignUpCommand {
        phoneNumber = phoneNumber.replace("-", "");
    }
}
