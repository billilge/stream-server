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
            thumbnailUrlOf(summary.thumbnailFileId()),
            summary.applyStartAt(),
            summary.applyEndAt(),
            summary.recruitStatus(),
            summary.daysUntilDeadline()
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
