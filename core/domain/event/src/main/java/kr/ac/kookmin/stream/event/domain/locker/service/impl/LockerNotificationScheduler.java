package kr.ac.kookmin.stream.event.domain.locker.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.DateUtil;
import kr.ac.kookmin.stream.common.OutboxWriter;
import kr.ac.kookmin.stream.common.event.LockerApplyClosingTomorrow;
import kr.ac.kookmin.stream.common.event.LockerUsageEndingSoon;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import kr.ac.kookmin.stream.event.domain.locker.repository.LockerApplicationRepository;
import kr.ac.kookmin.stream.event.domain.locker.repository.LockerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사물함 예약 알림을 아웃박스에 기록한다. 매일 10시에 내일 신청이 마감되는 회차와 이용 기간이 곧 끝나는 회차를 고른다.
 * <p>
 * 발송 기록을 따로 남기지 않고 날짜로 대상을 고르므로, 서버가 그 시각에 내려가 있었으면 그날 알림은 건너뛴다.
 */
@Component
@RequiredArgsConstructor
class LockerNotificationScheduler {

    // 이용 종료일 며칠 전에 만료 임박 알림을 보낼지
    private static final long USAGE_ENDING_NOTICE_DAYS = 3;

    private final LockerRepository lockerRepository;
    private final LockerApplicationRepository lockerApplicationRepository;
    private final OutboxWriter outboxWriter;

    @Scheduled(cron = "0 0 10 * * *", zone = "Asia/Seoul")
    @Transactional
    public void recordDailyNotifications() {
        LocalDateTime now = LocalDateTime.now(DateUtil.KST);
        LocalDate today = now.toLocalDate();

        recordApplyClosingTomorrow(now, today.plusDays(1));
        recordUsageEndingSoon(today.plusDays(USAGE_ENDING_NOTICE_DAYS));
    }

    // 아직 신청 기간이 시작되지 않은 회차는 지금 신청할 수 없어 뺀다
    private void recordApplyClosingTomorrow(LocalDateTime now, LocalDate tomorrow) {
        List<LockerPeriod> periods = lockerRepository.findPublishedPeriodsApplyEndingBetween(
            tomorrow.atStartOfDay(), tomorrow.plusDays(1).atStartOfDay());
        for (LockerPeriod period : periods) {
            if (now.isBefore(period.getApplyStartAt())) {
                continue;
            }
            List<Long> applicantIds = lockerApplicationRepository.findMemberIds(period.getId());
            outboxWriter.record(new LockerApplyClosingTomorrow(period.getId(), applicantIds));
        }
    }

    // 신청자가 없으면 받을 사람이 없어 기록하지 않는다
    private void recordUsageEndingSoon(LocalDate usageEndAt) {
        for (LockerPeriod period : lockerRepository.findPublishedPeriodsUsageEndingOn(usageEndAt)) {
            List<Long> applicantIds = lockerApplicationRepository.findMemberIds(period.getId());
            if (!applicantIds.isEmpty()) {
                outboxWriter.record(new LockerUsageEndingSoon(period.getId(), applicantIds));
            }
        }
    }
}
