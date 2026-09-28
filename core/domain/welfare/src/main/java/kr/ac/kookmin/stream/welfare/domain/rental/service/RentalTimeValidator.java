package kr.ac.kookmin.stream.welfare.domain.rental.service;

import java.time.LocalTime;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;

// 대여 신청 시각(영업시간 10~17시, 점심시간 12~13시 제외)을 검증한다.
// fee 도메인의 FeeAmountCalculator/TossTransferLinkGenerator와 같은 패턴 — 상태 없는 순수 검증 로직은 정적 유틸로 둔다.
public final class RentalTimeValidator {

    private static final LocalTime OPEN = LocalTime.of(10, 0);
    private static final LocalTime CLOSE = LocalTime.of(17, 0);
    private static final LocalTime LUNCH_START = LocalTime.of(12, 0);
    private static final LocalTime LUNCH_END = LocalTime.of(13, 0);

    private RentalTimeValidator() {}

    public static void validate(int hour, int minute) {
        LocalTime rentalTime = LocalTime.of(hour, minute);
        if (rentalTime.isBefore(OPEN) || rentalTime.isAfter(CLOSE)) {
            throw new BusinessException(RentalErrorCode.INVALID_RENTAL_TIME_RANGE);
        }
        if (!rentalTime.isBefore(LUNCH_START) && rentalTime.isBefore(LUNCH_END)) {
            throw new BusinessException(RentalErrorCode.INVALID_RENTAL_TIME_LUNCH_BREAK);
        }
    }
}
