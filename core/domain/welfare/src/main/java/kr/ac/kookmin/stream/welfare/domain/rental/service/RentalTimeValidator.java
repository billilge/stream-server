package kr.ac.kookmin.stream.welfare.domain.rental.service;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;

// 대여 신청 시각(영업시간 10~17시, 점심시간 12~13시 제외, 현재 시각 5분 뒤부터)을 검증한다.
// fee 도메인의 TossTransferLinkGenerator와 같은 패턴 — 상태 없는 순수 검증 로직은 정적 유틸로 둔다.
public final class RentalTimeValidator {

    private static final LocalTime OPEN = LocalTime.of(10, 0);
    private static final LocalTime CLOSE = LocalTime.of(17, 0);
    private static final LocalTime LUNCH_START = LocalTime.of(12, 0);
    private static final LocalTime LUNCH_END = LocalTime.of(13, 0);
    private static final int MIN_LEAD_MINUTES = 5;

    private RentalTimeValidator() {}

    /**
     * @param now 비교 기준이 되는 현재 시각(KST). 시계를 호출부에 둬서 이 검증을 순수 함수로 유지한다
     */
    public static void validate(int hour, int minute, LocalTime now) {
        LocalTime rentalTime = LocalTime.of(hour, minute);
        if (rentalTime.isBefore(OPEN) || rentalTime.isAfter(CLOSE)) {
            throw new BusinessException(RentalErrorCode.INVALID_RENTAL_TIME_RANGE);
        }
        if (!rentalTime.isBefore(LUNCH_START) && rentalTime.isBefore(LUNCH_END)) {
            throw new BusinessException(RentalErrorCode.INVALID_RENTAL_TIME_LUNCH_BREAK);
        }
        // 앱이 지금+5분(초 버림)을 기본값으로 채우므로 now도 분 단위로 잘라 비교한다.
        // now에 더하면 23:55 이후 자정을 넘어 되감기므로, 영업시간 안으로 검증된 rentalTime 쪽에서 뺀다.
        if (rentalTime.minusMinutes(MIN_LEAD_MINUTES).isBefore(now.truncatedTo(ChronoUnit.MINUTES))) {
            throw new BusinessException(RentalErrorCode.INVALID_RENTAL_TIME_PAST);
        }
    }
}
