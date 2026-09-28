package kr.ac.kookmin.stream.event.domain.locker.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 회원의 사물함 신청 내역 한 건. 신청에 어느 회차의 어떤 사물함이었는지와 배정 상태를 덧붙인 읽기 모델이다.
 */
public record LockerApplicationSummary(
    Long applicationId,
    Long lockerPeriodId,
    String lockerPeriodName,
    LockerApplicationStatus applicationStatus,
    LocalDateTime appliedAt,
    LocalDate usageStartAt,
    LocalDate usageEndAt,
    String lockerLabel
) {

    /**
     * @param today 배정 상태를 판정할 기준일
     */
    public static LockerApplicationSummary of(
        LockerApplication application,
        LockerPeriod period,
        Locker locker,
        LocalDate today
    ) {
        return new LockerApplicationSummary(
            application.getId(),
            period.getId(),
            period.getName(),
            LockerApplicationStatus.from(period, today),
            application.getAppliedAt(),
            period.getUsageStartAt(),
            period.getUsageEndAt(),
            locker.getLockerLabel()
        );
    }
}
