package kr.ac.kookmin.stream.api.app.event.locker.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplyStatus;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;

/**
 * @param status 신청 접수 상태. 판정에 초 절삭·사용 종료 검사가 들어 있어 서버가 계산해 내려준다
 */
public record LockerPeriodResponse(
    Long lockerPeriodId,
    LocalDateTime applyStartAt,
    LocalDateTime applyEndAt,
    LocalDate usageStartDate,
    LocalDate usageEndDate,
    LockerApplyStatus status
) {

    public static LockerPeriodResponse of(LockerPeriod period, LocalDateTime now) {
        return new LockerPeriodResponse(
            period.getId(),
            period.getApplyStartAt(),
            period.getApplyEndAt(),
            period.getUsageStartAt(),
            period.getUsageEndAt(),
            period.calculateApplyStatus(now)
        );
    }
}
