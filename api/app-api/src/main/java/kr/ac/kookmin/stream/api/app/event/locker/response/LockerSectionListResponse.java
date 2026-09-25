package kr.ac.kookmin.stream.api.app.event.locker.response;

import java.util.List;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionSummary;

public record LockerSectionListResponse(List<LockerSectionResponse> sections) {

    /**
     * @param mySectionId 조회한 회원이 신청한 사물함이 속한 구역. 신청하지 않았으면 {@code null}
     */
    public static LockerSectionListResponse of(List<LockerSectionSummary> sections, Long mySectionId) {
        return new LockerSectionListResponse(sections.stream()
            .map(summary -> LockerSectionResponse.of(summary, mySectionId))
            .toList());
    }
}
