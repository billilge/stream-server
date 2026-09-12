package kr.ac.kookmin.stream.event.domain.event.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 행사 목록 한 건. 모집 상태와 마감까지 남은 일수는 저장값이 아니라 조회 시점 기준으로 계산한다.
 */
public record EventSummary(
    Long eventId,
    String title,
    String target,
    LocalDateTime eventStartAt,
    Long thumbnailFileId,
    LocalDateTime applyStartAt,
    LocalDateTime applyEndAt,
    RecruitStatus recruitStatus,
    Integer daysUntilDeadline
) {

    public static EventSummary of(Event event, long applicantCount, LocalDateTime now) {
        // 모집 상태 판정은 폼 조회·신청과 같은 기준을 써야 하므로 Event의 계산을 그대로 쓴다
        RecruitStatus recruitStatus = event.calculateRecruitStatus(now, applicantCount);
        return new EventSummary(
            event.getId(),
            event.getTitle(),
            event.getTarget(),
            event.getEventStartAt(),
            thumbnailFileIdOf(event.getImageIds()),
            event.getApplyStartAt(),
            event.getApplyEndAt(),
            recruitStatus,
            calculateDaysUntilDeadline(event, recruitStatus, now)
        );
    }

    // D-Day 배지는 모집 중일 때만 노출하므로 그 외 상태에서는 값을 내려보내지 않는다
    private static Integer calculateDaysUntilDeadline(Event event, RecruitStatus recruitStatus, LocalDateTime now) {
        if (recruitStatus != RecruitStatus.OPEN) {
            return null;
        }
        return (int) ChronoUnit.DAYS.between(now.toLocalDate(), event.getApplyEndAt().toLocalDate());
    }

    private static Long thumbnailFileIdOf(List<Long> imageIds) {
        return imageIds == null || imageIds.isEmpty() ? null : imageIds.getFirst();
    }
}
