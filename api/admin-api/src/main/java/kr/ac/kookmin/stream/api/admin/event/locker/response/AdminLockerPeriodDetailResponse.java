package kr.ac.kookmin.stream.api.admin.event.locker.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodStatus;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodSummary;

/**
 * 수정 화면의 기존 값을 채우는 데 쓴다. 연도·학기는 수정할 수 없지만 화면에 보여줘야 해서 따로 내려준다.
 */
public record AdminLockerPeriodDetailResponse(
    Long lockerPeriodId,
    String lockerPeriodName,
    int year,
    int semester,
    LocalDateTime applyStartAt,
    LocalDateTime applyEndAt,
    LocalDate usageStartDate,
    LocalDate usageEndDate,
    LockerPeriodStatus periodStatus
) {

    public static AdminLockerPeriodDetailResponse from(LockerPeriodSummary summary) {
        LockerPeriod period = summary.period();
        return new AdminLockerPeriodDetailResponse(
            period.getId(),
            period.getName(),
            period.getYear(),
            period.getSemester(),
            period.getApplyStartAt(),
            period.getApplyEndAt(),
            period.getUsageStartAt(),
            period.getUsageEndAt(),
            summary.status()
        );
    }
}
