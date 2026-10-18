package kr.ac.kookmin.stream.api.admin.event.locker.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodStatus;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodSummary;

/**
 * @param currentPeriod 진행 중인 회차. 없으면 {@code null}. 회차 기간은 서로 겹치지 않으므로 최대 한 건이다
 * @param periods       전체 회차. 진행 중인 회차도 함께 들어 있다
 */
public record AdminLockerPeriodListResponse(
    Period currentPeriod,
    List<Period> periods
) {

    public static AdminLockerPeriodListResponse from(List<LockerPeriodSummary> summaries) {
        return new AdminLockerPeriodListResponse(
            summaries.stream()
                .filter(summary -> summary.status() == LockerPeriodStatus.IN_PROGRESS)
                .findFirst()
                .map(Period::from)
                .orElse(null),
            summaries.stream()
                .map(Period::from)
                .toList()
        );
    }

    public record Period(
        Long lockerPeriodId,
        String lockerPeriodName,
        LocalDateTime applyStartAt,
        LocalDateTime applyEndAt,
        LocalDate usageStartDate,
        LocalDate usageEndDate,
        LockerPeriodStatus periodStatus
    ) {

        public static Period from(LockerPeriodSummary summary) {
            LockerPeriod period = summary.period();
            return new Period(
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
}
