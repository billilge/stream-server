package kr.ac.kookmin.stream.api.app.event.locker.response;

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

    public static LockerSectionResponse from(LockerSectionSummary summary) {
        return new LockerSectionResponse(
            summary.sectionId(),
            summary.label(),
            summary.availableCount(),
            summary.totalCount(),
            summary.availabilityStatus(),
            summary.hasMine()
        );
    }
}
