package kr.ac.kookmin.stream.event;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.event.domain.event.domain.EventSummary;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;

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

    public static EventListItemResponse from(EventSummary summary) {
        return new EventListItemResponse(
            summary.eventId(),
            summary.title(),
            summary.target(),
            summary.eventStartAt(),
            // 대표 이미지 파일 id는 summary.thumbnailFileId()로 알 수 있으나
            // 파일 키 → 공개 URL 조립(#17)이 아직 없어 URL은 내려보내지 않는다
            null,
            summary.applyStartAt(),
            summary.applyEndAt(),
            summary.recruitStatus(),
            summary.daysUntilDeadline()
        );
    }
}
