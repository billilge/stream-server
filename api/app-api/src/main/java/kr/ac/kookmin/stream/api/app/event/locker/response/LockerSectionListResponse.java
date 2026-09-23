package kr.ac.kookmin.stream.api.app.event.locker.response;

import java.util.List;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionSummary;

public record LockerSectionListResponse(List<LockerSectionResponse> sections) {

    public static LockerSectionListResponse from(List<LockerSectionSummary> sections) {
        return new LockerSectionListResponse(sections.stream().map(LockerSectionResponse::from).toList());
    }
}
