package kr.ac.kookmin.stream.event.domain.locker.domain;

/**
 * 사물함 구역 목록 한 건. 세 개수 모두 저장값이 아니라 조회 시점에 센다.
 *
 * @param availableCount 선택 가능한 사물함 수. 사용 중지된 사물함과 이 회차에 배정된 사물함은 빠진다
 * @param totalCount     구역의 전체 사물함 수. 사용 중지된 사물함도 포함한다
 * @param assignedCount  이 회차에 배정된 사물함 수
 */
public record LockerSectionSummary(
    Long sectionId,
    String label,
    int availableCount,
    int totalCount,
    int assignedCount,
    SectionAvailabilityStatus availabilityStatus
) {
}
