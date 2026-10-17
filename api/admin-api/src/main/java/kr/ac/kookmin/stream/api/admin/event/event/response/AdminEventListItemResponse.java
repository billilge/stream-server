package kr.ac.kookmin.stream.api.admin.event.event.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.event.domain.event.domain.EventManageSummary;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;

/**
 * @param capacity 선착순 모집의 정원. 상시 모집이면 {@code null}이고 화면에 "인원 제한 없음"으로 보인다
 */
public record AdminEventListItemResponse(
    Long eventId,
    String title,
    long applicantCount,
    RecruitType recruitType,
    Integer capacity,
    RecruitStatus recruitStatus,
    boolean isPublished,
    LocalDateTime applyStartAt,
    LocalDateTime applyEndAt
) {

    public static AdminEventListItemResponse from(EventManageSummary summary) {
        return new AdminEventListItemResponse(
            summary.eventId(),
            summary.title(),
            summary.applicantCount(),
            summary.recruitType(),
            summary.capacity(),
            summary.recruitStatus(),
            summary.published(),
            summary.applyStartAt(),
            summary.applyEndAt()
        );
    }
}
