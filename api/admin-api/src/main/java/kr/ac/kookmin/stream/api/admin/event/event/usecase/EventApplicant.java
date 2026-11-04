package kr.ac.kookmin.stream.api.admin.event.event.usecase;

import java.util.List;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationDetail;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;

/**
 * 신청 현황 한 줄을 만들 재료. 신청(event)과 신청자(member)가 서로 다른 도메인이라 UseCase가 짝지어 담는다.
 * 화면용 가공은 응답 DTO가 한다.
 *
 * @param member            탈퇴(소프트 삭제)한 회원의 신청이면 {@code null}
 * @param answeredQuestions 행사의 질문 전체와 이 신청의 답변. 질문과 답변 짝짓기는 신청자 상세와 같은
 *                          {@link EventApplicationDetail.AnsweredQuestion#listOf}를 쓴다
 */
public record EventApplicant(
    EventApplication application,
    Member member,
    List<EventApplicationDetail.AnsweredQuestion> answeredQuestions
) {

    public String memberName() {
        return member == null ? null : member.getName();
    }

    public String memberStudentId() {
        return member == null ? null : member.getStudentId();
    }
}
