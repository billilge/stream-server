package kr.ac.kookmin.stream.api.admin.welfare.feedback.usecase;

import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;

// 질문에 작성자를 붙인 결과. DTO 변환은 Controller가 한다.
// member는 탈퇴(소프트 삭제)한 회원의 질문이면 null일 수 있다.
public record MemberFeedback(Feedback feedback, Member member) {

    public String memberName() {
        return member == null ? null : member.getName();
    }
}
