package kr.ac.kookmin.stream.event.domain.event.domain;

/**
 * 운영진 신청자 상세에 필요한 도메인 객체 묶음. 질문과 답변은 학생 앱 내 신청 상세와 같은 {@link EventApplicationDetail}로
 * 짝짓고, 신청 식별자와 신청자를 찾을 회원 식별자는 신청에서 꺼낸다.
 */
public record EventApplicationManageDetail(
    EventApplication application,
    EventApplicationDetail detail
) {
}
