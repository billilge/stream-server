package kr.ac.kookmin.stream.api.app.core.member.response;

import kr.ac.kookmin.stream.member.domain.member.domain.Member;

public record MemberResponse(
    String name,
    // 이메일을 등록하지 않은 회원은 null이다
    String email,
    // 하이픈 없이 숫자만 내려준다. 저장된 형식 그대로다
    String phoneNumber
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getName(), member.getEmail(), member.getPhoneNumber());
    }
}
