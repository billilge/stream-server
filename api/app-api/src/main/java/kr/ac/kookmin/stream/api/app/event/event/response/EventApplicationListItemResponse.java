package kr.ac.kookmin.stream.api.app.event.event.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationSummary;

public record EventApplicationListItemResponse(
    Long applicationId,
    Long eventId,
    String title,
    String thumbnailUrl,
    EventApplicationStatus applicationStatus,
    LocalDateTime appliedAt,
    LocalDateTime canceledAt
) {

    public static EventApplicationListItemResponse from(EventApplicationSummary summary) {
        return new EventApplicationListItemResponse(
            summary.applicationId(),
            summary.eventId(),
            summary.title(),
            thumbnailUrlOf(summary.thumbnailFileId()),
            summary.applicationStatus(),
            summary.appliedAt(),
            summary.canceledAt()
        );
    }

    /**
     * 대표 이미지 파일 id를 공개 URL로 바꾼다.
     * <p>
     * 파일 키 → 공개 URL 조립(#17)이 아직 없어 현재는 항상 비어 있다. #17이 머지되면 이 메서드만 채우면 된다.
     */
    private static String thumbnailUrlOf(Long thumbnailFileId) {
        return null;
    }
}
