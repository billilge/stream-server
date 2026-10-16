package kr.ac.kookmin.stream.api.admin.event.locker.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodStatus;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodSummary;

public record AdminLockerPeriodListItemResponse(
    Long lockerPeriodId,
    String lockerPeriodName,
    LocalDateTime applyStartAt,
    LocalDateTime applyEndAt,
    LocalDate usageStartDate,
    LocalDate usageEndDate,
    LockerPeriodStatus periodStatus
) {

    public static AdminLockerPeriodListItemResponse from(LockerPeriodSummary summary) {
        LockerPeriod period = summary.period();
        return new AdminLockerPeriodListItemResponse(
            period.getId(),
            period.getName(),
            period.getApplyStartAt(),
            period.getApplyEndAt(),
            period.getUsageStartAt(),
            period.getUsageEndAt(),
            summary.status()
        );
    }
}
