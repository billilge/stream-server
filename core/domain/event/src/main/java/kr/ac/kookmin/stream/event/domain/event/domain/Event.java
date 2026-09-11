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
        Long createdBy
    ) {
        return new Event(
            id, title, description, target, place, eventStartAt, eventEndAt, applyStartAt,
            applyEndAt, recruitType, imageIds, capacity, recruitStatus, createdBy
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
        if (recruitStatus == RecruitStatus.CLOSED) {
            return RecruitStatus.CLOSED;
        }
        if (now.isBefore(applyStartAt)) {
            return RecruitStatus.BEFORE_OPEN;
        }
        if (now.isAfter(applyEndAt)) {
            return RecruitStatus.CLOSED;
        }
        if (isCapacityFull(appliedCount)) {
            return RecruitStatus.CLOSED;
        }
        return RecruitStatus.OPEN;
    }

    /**
     * 정원이 찼는지 판정한다. 선착순 모집에만 정원 제한이 있고, 상시 모집은 인원 제한이 없다.
     * <p>
     * 모집 상태 계산은 정원 마감을 CLOSED로 합치지만, 신청 실패 사유는 기간 마감과 정원 마감을 구분해야 하므로 분리해 둔다.
     */
    public boolean isCapacityFull(long appliedCount) {
        return recruitType == RecruitType.FIRST_COME && appliedCount >= capacity;
    }
}
