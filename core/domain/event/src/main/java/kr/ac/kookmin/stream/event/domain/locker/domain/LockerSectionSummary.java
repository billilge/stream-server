package kr.ac.kookmin.stream.event.domain.locker.domain;

/**
 * 사물함 구역 목록 한 건. 전체·선택 가능 수는 저장값이 아니라 조회 시점에 센다.
 */
public record LockerSectionSummary(
    Long sectionId,
    String label,
    int availableCount,
    int totalCount,
    SectionAvailabilityStatus availabilityStatus
) {
}
