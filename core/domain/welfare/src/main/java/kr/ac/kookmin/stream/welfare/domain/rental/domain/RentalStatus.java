package kr.ac.kookmin.stream.welfare.domain.rental.domain;

import java.util.Set;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CommonErrorCode;

public enum RentalStatus {
    PENDING,
    CONFIRMED,
    REJECTED,
    CANCEL,
    RENTAL,
    RETURN_PENDING,
    RETURNED,
    RETURN_CONFIRMED;

    // 완전히 끝난 상태.
    private static final Set<RentalStatus> FINISHED_STATUSES = Set.of(RETURNED, CANCEL, REJECTED);

    // 아직 반납이 최종 확인되지 않은 상태.
    private static final Set<RentalStatus> AWAITING_RETURN_STATUSES = Set.of(RENTAL, RETURN_PENDING, RETURN_CONFIRMED);

    public static RentalStatus from(String value) {
        if (value == null) {
            return null;
        }
        try {
            return RentalStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }
    }

    /** 더 이상 진행되지 않는(완전히 끝난) 상태인지. */
    public boolean isFinished() {
        return FINISHED_STATUSES.contains(this);
    }

    /**
     * 이 상태의 대여 기록이 물품 재고를 차지하고 있는지. 진행 중이면 차지하고, 끝난 상태 중에는 이미 내준 소모품의
     * RETURNED만 차지한다. 상태 변경·기록 삭제 때 재고를 복구할지 차감할지 이 값으로 판단한다.
     */
    public boolean holdsStock(ItemType itemType) {
        if (!isFinished()) {
            return true;
        }
        return this == RETURNED && itemType == ItemType.CONSUMABLE;
    }

    public static Set<RentalStatus> finishedStatuses() {
        return FINISHED_STATUSES;
    }

    public static Set<RentalStatus> awaitingReturnStatuses() {
        return AWAITING_RETURN_STATUSES;
    }
}
