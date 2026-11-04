package kr.ac.kookmin.stream.api.admin.event.locker.response;

import java.util.List;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionSummary;

public record AdminLockerSectionListResponse(List<Section> sections) {

    public static AdminLockerSectionListResponse from(List<LockerSectionSummary> summaries) {
        return new AdminLockerSectionListResponse(summaries.stream()
            .map(Section::from)
            .toList());
    }

    /**
     * @param totalCount     구역 내 전체 사물함 수. 사용 중지된 사물함도 포함한다
     * @param availableCount 선택 가능한 사물함 수. 사용 중지된 사물함과 이 회차에 배정된 사물함은 빠진다
     * @param assignedCount  이 회차에 배정된 사물함 수. 배정된 뒤 사용 중지된 사물함도 센다
     */
    public record Section(
        Long sectionId,
        String section,
        int totalCount,
        int availableCount,
        int assignedCount
    ) {

        public static Section from(LockerSectionSummary summary) {
            return new Section(
                summary.sectionId(),
                summary.label(),
                summary.totalCount(),
                summary.availableCount(),
                summary.assignedCount()
            );
        }
    }
}
