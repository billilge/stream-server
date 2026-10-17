package kr.ac.kookmin.stream.event.domain.event.domain;

import java.time.LocalDateTime;

/**
 * 운영진 행사 목록 한 건. 학생 목록({@link EventSummary})과 달리 비공개 행사도 담고, 신청자 수·정원·공개 여부를 함께 싣는다.
 * 모집 상태는 저장값이 아니라 조회 시점 기준으로 계산한다.
 *
 * @param capacity 선착순 모집의 정원. 상시 모집은 인원 제한이 없어 {@code null}
 */
public record EventManageSummary(
    Long eventId,
    String title,
    long applicantCount,
    RecruitType recruitType,
    Integer capacity,
    RecruitStatus recruitStatus,
    boolean published,
    LocalDateTime applyStartAt,
    LocalDateTime applyEndAt
) {

    public static EventManageSummary of(Event event, long applicantCount, LocalDateTime now) {
        return new EventManageSummary(
            event.getId(),
            event.getTitle(),
            applicantCount,
            event.getRecruitType(),
            event.capacityLimit(),
            event.calculateRecruitStatus(now, applicantCount),
            event.isPublished(),
            event.getApplyStartAt(),
            event.getApplyEndAt()
        );
    }
}
