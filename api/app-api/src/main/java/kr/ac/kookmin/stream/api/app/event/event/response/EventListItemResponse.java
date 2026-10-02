package kr.ac.kookmin.stream.api.app.event.event.response;

import java.time.LocalDateTime;
import java.util.Map;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.event.domain.event.domain.EventSummary;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.file.domain.File;

public record EventListItemResponse(
    Long eventId,
    String title,
    String target,
    LocalDateTime eventStartAt,
    String thumbnailUrl,
    LocalDateTime applyStartAt,
    LocalDateTime applyEndAt,
    RecruitStatus recruitStatus,
    Integer daysUntilDeadline
) {

    public static EventListItemResponse from(EventSummary summary, Map<Long, File> filesById) {
        return new EventListItemResponse(
            summary.eventId(),
            summary.title(),
            summary.target(),
            summary.eventStartAt(),
            StorageUrlBuilder.build(summary.thumbnailFileId(), filesById),
            summary.applyStartAt(),
            summary.applyEndAt(),
            summary.recruitStatus(),
            summary.daysUntilDeadline()
        );
    }
}
