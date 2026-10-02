package kr.ac.kookmin.stream.api.app.event.locker.response;

import java.time.LocalDate;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationResult;

public record LockerApplyResponse(
    Long lockerApplicationId,
    Long lockerId,
    String lockerLabel,
    LocalDate usageStartDate,
    LocalDate usageEndDate
) {

    public static LockerApplyResponse from(LockerApplicationResult result) {
        return new LockerApplyResponse(
            result.application().getId(),
            result.locker().getId(),
            result.locker().getLockerLabel(),
            result.period().getUsageStartAt(),
            result.period().getUsageEndAt()
        );
    }
}
