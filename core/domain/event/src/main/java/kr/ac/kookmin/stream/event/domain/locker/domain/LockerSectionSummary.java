package kr.ac.kookmin.stream.event.domain.locker.domain;

import java.util.List;
import java.util.Set;

/**
 * 사물함 구역 목록 한 건. 전체·선택 가능 수와 표시 상태는 저장값이 아니라 조회 시점에 센다.
 * <p>
 * 조회한 회원에 따라 달라지는 값은 담지 않는다. 내 사물함이 있는 구역인지는 이 결과와
 * {@code LockerService#getLockerByMemberId}의 구역을 맞춰보면 나온다.
 */
public record LockerSectionSummary(
    Long sectionId,
    String label,
    int availableCount,
    int totalCount,
    SectionAvailabilityStatus availabilityStatus
) {

    /**
     * @param lockers          구역에 속한 사물함. 사물함이 없는 구역이면 빈 목록이다
     * @param appliedLockerIds 해당 운영 회차에 이미 신청된 사물함 식별자
     */
    public static LockerSectionSummary of(
        LockerSection section,
        List<Locker> lockers,
        Set<Long> appliedLockerIds
    ) {
        int totalCount = lockers.size();
        int availableCount = (int) lockers.stream()
            .filter(locker -> locker.isSelectable(appliedLockerIds.contains(locker.getId())))
            .count();

        return new LockerSectionSummary(
            section.getId(),
            section.getLabel(),
            availableCount,
            totalCount,
            SectionAvailabilityStatus.from(availableCount, totalCount)
        );
    }
}
