package kr.ac.kookmin.stream.event.domain.event.domain;

import java.time.LocalDateTime;

/**
 * 내 행사 신청 내역 한 건. 신청 자체의 상태·시각에 어떤 행사였는지를 덧붙인 읽기 모델이다.
 * <p>
 * 행사 목록({@link EventSummary})과 달리 모집 상태·D-Day는 싣지 않는다. 이미 신청한 건이라 지금 모집 중인지는
 * 화면에 쓰이지 않는다.
 */
public record EventApplicationSummary(
    Long applicationId,
    Long eventId,
    String title,
    Long thumbnailFileId,
    EventApplicationStatus applicationStatus,
    LocalDateTime appliedAt,
    LocalDateTime canceledAt
) {

    public static EventApplicationSummary of(EventApplication application, Event event) {
        return new EventApplicationSummary(
            application.getId(),
            event.getId(),
            event.getTitle(),
            event.thumbnailFileId(),
            application.getStatus(),
            application.getAppliedAt(),
            application.getCanceledAt()
        );
    }
}
