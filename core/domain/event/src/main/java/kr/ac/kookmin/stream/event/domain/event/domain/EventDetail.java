package kr.ac.kookmin.stream.event.domain.event.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 행사 상세 한 건. 목록과 마찬가지로 모집 상태와 마감까지 남은 일수는 저장값이 아니라 조회 시점 기준으로 계산한다.
 */
public record EventDetail(
    Long eventId,
    String title,
    String description,
    String target,
    String place,
    LocalDateTime eventStartAt,
    LocalDateTime eventEndAt,
    LocalDateTime applyStartAt,
    LocalDateTime applyEndAt,
    List<Long> imageIds,
    RecruitStatus recruitStatus,
    Integer daysUntilDeadline
) {

    public static EventDetail of(Event event, long applicantCount, LocalDateTime now) {
        // 모집 상태 판정은 목록·폼 조회·신청과 같은 기준을 써야 하므로 Event의 계산을 그대로 쓴다
        RecruitStatus recruitStatus = event.calculateRecruitStatus(now, applicantCount);
        return new EventDetail(
            event.getId(),
            event.getTitle(),
            event.getDescription(),
            event.getTarget(),
            event.getPlace(),
            event.getEventStartAt(),
            event.getEventEndAt(),
            event.getApplyStartAt(),
            event.getApplyEndAt(),
            event.getImageIds() == null ? List.of() : event.getImageIds(),
            recruitStatus,
            event.daysUntilDeadline(now, recruitStatus)
        );
    }
}
