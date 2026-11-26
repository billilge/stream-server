package kr.ac.kookmin.stream.event.domain.locker.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * @param assignedLocker {@link CurrentLockerStatus#ASSIGNED}일 때만 있다
 */
public record CurrentLocker(CurrentLockerStatus status, Locker assignedLocker) {

    /**
     * 회차는 겹치지 않아 최근 회차 신청이 시작됐으면 이전 회차 이용은 끝나 있으므로 최근 회차만 본다. 다만 운영진이 신청
     * 시작 전 회차에 미리 배정한 칸은 알림·"내 사물함"이 이미 배정으로 보여 주므로 홈도 배정으로 본다.
     *
     * @param latestPeriod 없으면 null
     */
    public static CurrentLocker of(LockerPeriod latestPeriod, List<LockerApplicationResult> applications,
        LocalDateTime now) {
        LocalDate today = now.toLocalDate();
        Optional<LockerApplicationResult> latestApplication = applications.stream()
            .filter(application -> latestPeriod != null && application.period().getId().equals(latestPeriod.getId()))
            .findFirst();

        Optional<LockerApplicationResult> assigned = latestApplication
            .filter(application -> LockerApplicationStatus.from(application.period(), today)
                == LockerApplicationStatus.ASSIGNED)
            .or(() -> findPreAssigned(applications, now));
        if (assigned.isPresent()) {
            return new CurrentLocker(CurrentLockerStatus.ASSIGNED, assigned.get().locker());
        }
        if (latestApplication.isPresent()) {
            return new CurrentLocker(CurrentLockerStatus.EXPIRED, null);
        }
        if (latestPeriod != null && latestPeriod.isApplyOpen(now)) {
            return new CurrentLocker(CurrentLockerStatus.APPLICABLE, null);
        }
        return new CurrentLocker(CurrentLockerStatus.NOT_APPLIED, null);
    }

    private static Optional<LockerApplicationResult> findPreAssigned(List<LockerApplicationResult> applications,
        LocalDateTime now) {
        return applications.stream()
            .filter(application -> application.period().calculateStatus(now) == LockerPeriodStatus.UPCOMING)
            .min(Comparator.comparing(application -> application.period().getApplyStartAt()));
    }
}
