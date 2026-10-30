package kr.ac.kookmin.stream.event.domain.event.domain;

import kr.ac.kookmin.stream.common.BusinessException;

/**
 * 모집 조건. 누가(신청 자격) 어떤 방식으로 몇 명까지(모집 방식·정원) 신청할 수 있는지를 함께 다룬다. 정원은 선착순 모집에만
 * 의미가 있다.
 * <p>
 * 선착순 정원이 1 이상이어야 한다는 규칙은 {@link #of}에서 검증하고, 상시 모집은 정원을 0으로 맞춘다. DB에서 복원할
 * 때는 저장된 값을 그대로 표준 생성자로 만든다.
 *
 * @param capacity    선착순 모집의 정원. 상시 모집이면 의미 없는 값 0이다
 * @param feePaidOnly 학생회비 납부자만 신청할 수 있는지. 신청 시 검증은 아직 없다(후속 작업)
 */
public record RecruitPolicy(RecruitType type, int capacity, boolean feePaidOnly) {

    public static RecruitPolicy of(RecruitType type, Integer capacity, boolean feePaidOnly) {
        validateCapacity(type, capacity);
        return new RecruitPolicy(type, type == RecruitType.FIRST_COME ? capacity : 0, feePaidOnly);
    }

    /**
     * 운영진 화면에 보여줄 정원. 상시 모집은 인원 제한이 없어 {@code null}이다(저장값 0은 의미가 없다).
     */
    public Integer capacityLimit() {
        return type == RecruitType.FIRST_COME ? capacity : null;
    }

    /**
     * 정원이 찼는지 판정한다. 선착순 모집에만 정원 제한이 있고, 상시 모집은 인원 제한이 없다.
     */
    public boolean isFull(long appliedCount) {
        return type == RecruitType.FIRST_COME && appliedCount >= capacity;
    }

    /**
     * 정원이 이미 받은 신청 수보다 적은지. 상시 모집은 인원 제한이 없어 보지 않는다.
     */
    public boolean isCapacityBelow(long appliedCount) {
        return type == RecruitType.FIRST_COME && capacity < appliedCount;
    }

    // 상시 모집은 인원 제한이 없어 정원을 보지 않는다
    private static void validateCapacity(RecruitType type, Integer capacity) {
        if (type == RecruitType.FIRST_COME && (capacity == null || capacity < 1)) {
            throw new BusinessException(EventErrorCode.EVENT_INVALID_CAPACITY);
        }
    }
}
