package kr.ac.kookmin.stream.api.app.event.locker.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationResult;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationStatus;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;

public record MyLockerApplicationListResponse(List<Item> applications) {

    /**
     * @param today 배정 상태(배정완료·이용종료)를 판정할 기준일
     */
    public static MyLockerApplicationListResponse of(List<LockerApplicationResult> applications, LocalDate today) {
        return new MyLockerApplicationListResponse(applications.stream()
            .map(application -> Item.of(application, today))
            .toList());
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

        public static Item of(LockerApplicationResult application, LocalDate today) {
            LockerPeriod period = application.period();
            return new Item(
                application.application().getId(),
                period.getId(),
                period.getName(),
                LockerApplicationStatus.from(period, today),
                application.application().getAppliedAt(),
                period.getUsageStartAt(),
                period.getUsageEndAt(),
                application.locker().getLockerLabel()
            );
        }
    }
}
