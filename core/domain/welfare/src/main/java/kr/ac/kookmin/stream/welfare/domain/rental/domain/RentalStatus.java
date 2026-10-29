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

    // 더 이상 손댈 일이 없는(재고가 이미 돌아왔거나 애초에 나가지 않은) 상태. 물품 삭제 차단·중복 대여 차단·
    // 재고 복구 중복 방지가 모두 "이 셋이 아니면 진행 중"이라는 같은 기준을 쓴다.
    private static final Set<RentalStatus> FINISHED_STATUSES = Set.of(RETURNED, CANCEL, REJECTED);

    // 아직 운영진이 반납을 최종 확인(RETURNED)하지 않은, "들고 있거나 반납 절차가 진행 중인" 상태.
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

    /** 아직 반납이 최종 확인되지 않은(들고 있거나 반납 절차가 진행 중인) 상태인지. */
    public boolean isAwaitingReturn() {
        return AWAITING_RETURN_STATUSES.contains(this);
    }

    /** 취소되거나 반려된(물건이 애초에 나가지 않은 것으로 되돌리는) 상태인지. */
    public boolean isCancelledOrRejected() {
        return this == CANCEL || this == REJECTED;
    }

    public static Set<RentalStatus> finishedStatuses() {
        return FINISHED_STATUSES;
    }
}
