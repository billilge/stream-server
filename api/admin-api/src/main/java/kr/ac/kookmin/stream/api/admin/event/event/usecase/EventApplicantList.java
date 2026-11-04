package kr.ac.kookmin.stream.api.admin.event.event.usecase;

import java.util.List;
import kr.ac.kookmin.stream.event.domain.event.domain.EventManageDetail;

/**
 * 엑셀 내보내기를 만들 재료. 신청 현황과 같은 조건의 신청을 페이지 없이 모두 담는다.
 *
 * @param event      행사와 노출 순서대로 정렬된 질문. 질문은 답변 열의 헤더가 된다
 * @param applicants 신청 순서
 */
public record EventApplicantList(EventManageDetail event, List<EventApplicant> applicants) {
}
