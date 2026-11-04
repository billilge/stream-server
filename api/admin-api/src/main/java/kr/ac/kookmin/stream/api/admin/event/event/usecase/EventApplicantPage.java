package kr.ac.kookmin.stream.api.admin.event.event.usecase;

import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventManageDetail;

/**
 * 신청 현황 화면을 만들 재료. 화면 제목·답변 열 헤더·신청 수는 행사 상세에서 가져온다.
 *
 * @param event                 행사, 노출 순서대로 정렬된 질문, 유효(APPLIED) 신청자 수
 * @param totalApplicationCount 취소를 포함한 전체 신청 수. 검색·필터와 상관없다
 */
public record EventApplicantPage(
    EventManageDetail event,
    long totalApplicationCount,
    PageResult<EventApplicant> applicants
) {
}
