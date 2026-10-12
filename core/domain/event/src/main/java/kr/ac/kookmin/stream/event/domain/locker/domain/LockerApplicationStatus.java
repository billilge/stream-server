package kr.ac.kookmin.stream.event.domain.locker.domain;

import java.time.LocalDate;

/**
 * 내 사물함 신청의 배정 상태. 신청이 곧 배정이라 신청 대기 상태는 없고, 사용 기간이 끝났는지로만 가른다.
 */
public enum LockerApplicationStatus {

    ASSIGNED,
    EXPIRED;

    public static LockerApplicationStatus from(LockerPeriod period, LocalDate today) {
        return period.isUsageEnded(today) ? EXPIRED : ASSIGNED;
    }
}
