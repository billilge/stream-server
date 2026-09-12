package kr.ac.kookmin.stream.event.domain.event.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Event {

    private Long id;
    private String title;
    private String description;
    private String target;
    private String place;
    private LocalDateTime eventStartAt;
    private LocalDateTime eventEndAt;
    private LocalDateTime applyStartAt;
    private LocalDateTime applyEndAt;
    private RecruitType recruitType;
    private List<Long> imageIds;
    private int capacity;
    private RecruitStatus recruitStatus;
    private boolean published;
    private Long createdBy;

    public static Event of(
        Long id,
        String title,
        String description,
        String target,
        String place,
        LocalDateTime eventStartAt,
        LocalDateTime eventEndAt,
        LocalDateTime applyStartAt,
        LocalDateTime applyEndAt,
        RecruitType recruitType,
        List<Long> imageIds,
        int capacity,
        RecruitStatus recruitStatus,
        boolean published,
        Long createdBy
    ) {
        return new Event(
            id, title, description, target, place, eventStartAt, eventEndAt, applyStartAt,
            applyEndAt, recruitType, imageIds, capacity, recruitStatus, published, createdBy
        );
    }

    /**
     * 현재 시각과 신청자 수를 반영한 모집 상태를 계산한다.
     * <p>
     * 저장된 recruitStatus는 운영진의 강제 마감만을 뜻하므로, 신청 기간과 잔여 정원을 함께 봐야 실제 상태가 나온다.
     * 행사 상세 조회·신청서 폼 조회·행사 신청이 같은 기준을 써야 하므로 도메인에 둔다.
     *
     * @param appliedCount status가 APPLIED인 신청 수. 선착순 모집이 아니면 쓰이지 않는다
     */
    public RecruitStatus calculateRecruitStatus(LocalDateTime now, long appliedCount) {
        if (isForceClosed() || isAfterApplyPeriod(now)) {
            return RecruitStatus.CLOSED;
        }
        if (isBeforeApplyPeriod(now)) {
            return RecruitStatus.BEFORE_OPEN;
        }
        if (isCapacityFull(appliedCount)) {
            return RecruitStatus.CLOSED;
        }
        return RecruitStatus.OPEN;
    }

    /**
     * 정원이 찼는지 판정한다. 선착순 모집에만 정원 제한이 있고, 상시 모집은 인원 제한이 없다.
     */
    public boolean isCapacityFull(long appliedCount) {
        return recruitType == RecruitType.FIRST_COME && appliedCount >= capacity;
    }

    /**
     * 정원 때문에만 닫힌 상태인지. 모집 상태 계산은 강제 마감·기간 종료·정원 마감을 모두 CLOSED로 합치지만,
     * 신청 실패 사유는 이 둘을 구분해야 하므로 정원이 유일한 사유일 때를 따로 판정한다.
     */
    public boolean isClosedByCapacityOnly(LocalDateTime now, long appliedCount) {
        return !isForceClosed()
            && !isBeforeApplyPeriod(now)
            && !isAfterApplyPeriod(now)
            && isCapacityFull(appliedCount);
    }

    /** 운영진이 강제로 마감했는지. 저장된 recruitStatus는 이 뜻만 갖는다. */
    private boolean isForceClosed() {
        return recruitStatus == RecruitStatus.CLOSED;
    }

    private boolean isBeforeApplyPeriod(LocalDateTime now) {
        return now.isBefore(applyStartAt);
    }

    private boolean isAfterApplyPeriod(LocalDateTime now) {
        return now.isAfter(applyEndAt);
    }
}
