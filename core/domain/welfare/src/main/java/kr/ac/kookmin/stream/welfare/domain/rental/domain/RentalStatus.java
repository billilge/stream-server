package kr.ac.kookmin.stream.welfare.domain.rental.domain;

import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CommonErrorCode;

public enum RentalStatus {
    PENDING,
    CONFIRMED,
    REJECTED,
    CANCEL,
    RENTAL,
    /** 반납 기한이 지났는데 아직 반납 절차(RETURN_PENDING 이후)를 시작하지 않은 상태. {@code RentalNotificationScheduler}가 매일 자동으로 세팅한다. */
    OVERDUE,
    RETURN_PENDING,
    RETURNED,
    RETURN_CONFIRMED;

    // 더 이상 손댈 일이 없는(재고가 이미 돌아왔거나 애초에 나가지 않은) 상태. 물품 삭제 차단·중복 대여 차단·
    // 재고 복구 중복 방지가 모두 "이 셋이 아니면 진행 중"이라는 같은 기준을 쓴다.
    private static final List<RentalStatus> FINISHED_STATUSES = List.of(RETURNED, CANCEL, REJECTED);

    // "지금 내가 들고 있는" 상태 — RENTAL과, 기한을 넘겨 전이된 OVERDUE. 연체됐다고 반납 대상에서 빠지면 안 된다.
    private static final List<RentalStatus> HOLDING_STATUSES = List.of(RENTAL, OVERDUE);

    // 아직 운영진이 반납을 최종 확인(RETURNED)하지 않은, "들고 있거나 반납 절차가 진행 중인" 상태.
    private static final List<RentalStatus> AWAITING_RETURN_STATUSES = List.of(RENTAL, OVERDUE, RETURN_PENDING, RETURN_CONFIRMED);

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

    public static List<RentalStatus> finishedStatuses() {
        return FINISHED_STATUSES;
    }

    public static List<RentalStatus> holdingStatuses() {
        return HOLDING_STATUSES;
    }
}
