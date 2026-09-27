package kr.ac.kookmin.stream.api.app.event.locker.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationStatus;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationSummary;

public record MyLockerApplicationListResponse(List<Item> applications) {

    public static MyLockerApplicationListResponse from(List<LockerApplicationSummary> applications) {
        return new MyLockerApplicationListResponse(applications.stream().map(Item::from).toList());
    }

    public record Item(
        Long lockerApplicationId,
        Long lockerPeriodId,
        String lockerPeriodName,
        LockerApplicationStatus applicationStatus,
        LocalDateTime appliedAt,
        LocalDate usageStartDate,
        LocalDate usageEndDate,
        String lockerLabel
    ) {

        public static Item from(LockerApplicationSummary application) {
            return new Item(
                application.applicationId(),
                application.lockerPeriodId(),
                application.lockerPeriodName(),
                application.applicationStatus(),
                application.appliedAt(),
                application.usageStartAt(),
                application.usageEndAt(),
                application.lockerLabel()
            );
        }
    }
}
