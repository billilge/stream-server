package kr.ac.kookmin.stream.api.admin.event.event.request;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;

public record EventApplicationSearchParams(
    @Schema(description = "이름 또는 학번 검색어", example = "김학생")
    String keyword,

    @Schema(description = "신청 상태 필터. APPLIED(신청완료) 또는 CANCELED(취소). 없으면 전체", example = "APPLIED")
    String status
) {

    // 빈 검색어는 검색하지 않는 것으로 본다
    public String toKeyword() {
        return keyword == null || keyword.isBlank() ? null : keyword.strip();
    }

    public EventApplicationStatus toStatus() {
        return EventApplicationStatus.from(status);
    }
}
