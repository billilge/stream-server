package kr.ac.kookmin.stream.api.admin.internal.home.response;

import java.util.List;

/**
 * 신청 섹션 후보 목록. 순서를 드래그로 바꾸므로 페이지로 자르지 않고 표시 순서대로 전체를 담는다.
 * 설정이 아직 없는 후보는 설정된 것들 뒤에 신청 마감 임박순으로 붙는다.
 */
public record AdminHomeApplySectionListResponse(List<AdminHomeApplySectionListItemResponse> content) {
}
