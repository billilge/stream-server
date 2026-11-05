package kr.ac.kookmin.stream.api.app.event.event.response;

import java.time.LocalDateTime;
import java.util.Map;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventSummary;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.file.domain.File;

/**
 * @param applicationId 요청한 학생이 이 행사에 낸 유효 신청의 식별자. 신청하지 않았거나 취소한 신청만 있으면
 *                      {@code null}이다. 모집 상태와 관계없이 내려준다
 */
public record EventListItemResponse(
    Long eventId,
    String title,
    LocalDateTime eventStartAt,
    String thumbnailUrl,
    LocalDateTime applyStartAt,
    LocalDateTime applyEndAt,
    RecruitStatus recruitStatus,
    Integer daysUntilDeadline,
    Long applicationId
) {

    public static EventListItemResponse of(
        EventSummary summary,
        Map<Long, File> filesById,
        Map<Long, EventApplication> applicationsByEventId
    ) {
        EventApplication application = applicationsByEventId.get(summary.eventId());
        return new EventListItemResponse(
            summary.eventId(),
            summary.title(),
            summary.eventStartAt(),
            StorageUrlBuilder.build(summary.thumbnailFileId(), filesById),
            summary.applyStartAt(),
            summary.applyEndAt(),
            summary.recruitStatus(),
            summary.daysUntilDeadline(),
            application == null ? null : application.getId()
        );
    }
}
