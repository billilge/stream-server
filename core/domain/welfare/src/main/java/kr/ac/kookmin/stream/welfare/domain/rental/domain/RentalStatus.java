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

    public static List<RentalStatus> finishedStatuses() {
        return FINISHED_STATUSES;
    }
}
