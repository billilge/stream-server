package kr.ac.kookmin.stream.event.domain.locker.domain;

import java.time.LocalDateTime;

/**
 * 운영 회차와 그 시점의 진행 상태. 상태는 저장하지 않고 조회할 때 계산하므로 회차와 따로 들고 다닌다.
 */
public record LockerPeriodSummary(LockerPeriod period, LockerPeriodStatus status) {

    public static LockerPeriodSummary of(LockerPeriod period, LocalDateTime now) {
        return new LockerPeriodSummary(period, period.calculateStatus(now));
    }
}
