package kr.ac.kookmin.stream.api.admin.event.event.usecase;

import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationDetail;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;

/**
 * 신청자 상세를 만들 재료. 질문과 답변 짝짓기는 학생 앱 내 신청 상세와 같은 {@link EventApplicationDetail}을 쓴다.
 *
 * @param member 탈퇴(소프트 삭제)한 회원의 신청이면 {@code null}
 */
public record EventApplicantDetail(EventApplication application, Member member, EventApplicationDetail detail) {
}
