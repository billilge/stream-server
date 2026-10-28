package kr.ac.kookmin.stream.welfare.domain.rental.domain;

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
}
