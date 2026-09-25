package kr.ac.kookmin.stream.api.app.event.locker.response;

import java.util.Objects;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionSummary;
import kr.ac.kookmin.stream.event.domain.locker.domain.SectionAvailabilityStatus;

public record LockerSectionResponse(
    Long sectionId,
    String section,
    int availableCount,
    int totalCount,
    SectionAvailabilityStatus availabilityStatus,
    boolean hasMine
) {

    /**
     * @param mySectionId 조회한 회원이 신청한 사물함이 속한 구역. 신청하지 않았으면 {@code null}
     */
    public static LockerSectionResponse of(LockerSectionSummary summary, Long mySectionId) {
        return new LockerSectionResponse(
            summary.sectionId(),
            summary.label(),
            summary.availableCount(),
            summary.totalCount(),
            summary.availabilityStatus(),
            Objects.equals(summary.sectionId(), mySectionId)
        );
    }
}
