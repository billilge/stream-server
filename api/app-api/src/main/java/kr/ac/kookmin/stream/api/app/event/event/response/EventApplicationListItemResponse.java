package kr.ac.kookmin.stream.api.app.event.event.response;

import java.time.LocalDateTime;
import java.util.Map;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationSummary;
import kr.ac.kookmin.stream.file.domain.File;

public record EventApplicationListItemResponse(
    Long applicationId,
    Long eventId,
    String title,
    String thumbnailUrl,
    EventApplicationStatus applicationStatus,
    LocalDateTime appliedAt,
    LocalDateTime canceledAt
) {

    public static EventApplicationListItemResponse from(EventApplicationSummary summary, Map<Long, File> filesById) {
        return new EventApplicationListItemResponse(
            summary.applicationId(),
            summary.eventId(),
            summary.title(),
            StorageUrlBuilder.build(summary.thumbnailFileId(), filesById),
            summary.applicationStatus(),
            summary.appliedAt(),
            summary.canceledAt()
        );
    }
}
