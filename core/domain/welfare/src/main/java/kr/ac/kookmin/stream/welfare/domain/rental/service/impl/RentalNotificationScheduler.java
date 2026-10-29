package kr.ac.kookmin.stream.welfare.domain.rental.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import kr.ac.kookmin.stream.common.DateUtil;
import kr.ac.kookmin.stream.common.OutboxWriter;
import kr.ac.kookmin.stream.common.event.RentalDueSoon;
import kr.ac.kookmin.stream.common.event.RentalOverdue;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.service.RentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 대여 반납 알림을 아웃박스에 기록한다.
 * <ul>
 *   <li>반납 기한 임박: 10분마다 돌며, "반납 기한 1시간 전" 시각이 직전 실행 이후부터 지금까지 사이에 든 대여를 고른다.</li>
 *   <li>반납 연체: 주말을 뺀 매일 10시에 기한이 지난 대여를 고른다. 반납할 때까지 평일마다 간다.</li>
 * </ul>
 * 발송 기록을 따로 남기지 않으므로, 서버가 그 시각에 내려가 있었으면 그 회차 알림은 건너뛴다.
 */
@Component
@RequiredArgsConstructor
class RentalNotificationScheduler {

    // 반납 기한 임박 확인 간격. recordDueSoon의 cron과 맞춰야 구간이 비거나 겹치지 않는다
    private static final int DUE_SOON_CHECK_INTERVAL_MINUTES = 10;
    private static final long DUE_SOON_LEAD_HOURS = 1;

    private final RentalHistoryService rentalHistoryService;
    private final OutboxWriter outboxWriter;

    // 실행이 조금 늦어져도 같은 구간을 보도록, 지금 시각을 확인 간격 단위로 내린 시각까지를 본다
    @Scheduled(cron = "0 */10 * * * *", zone = "Asia/Seoul")
    @Transactional
    public void recordDueSoon() {
        LocalDateTime windowEnd = floorToCheckInterval(LocalDateTime.now(DateUtil.KST));
        LocalDateTime windowStart = windowEnd.minusMinutes(DUE_SOON_CHECK_INTERVAL_MINUTES);

        for (RentalRecord rental : rentalHistoryService.getAllReturnRequiredRentals()) {
            LocalDateTime notifyAt = rental.dueAt().minusHours(DUE_SOON_LEAD_HOURS);
            if (notifyAt.isAfter(windowStart) && !notifyAt.isAfter(windowEnd)) {
                outboxWriter.record(new RentalDueSoon(
                    rental.history().getMemberId(), rental.history().getId(), rental.itemName()));
            }
        }
    }

    // 기한이 지났으면 매번 알림을 보낸다("반납할 때까지 평일마다 간다") — 상태 전이 없이 매번 dueAt만 다시 본다.
    @Scheduled(cron = "0 0 10 * * MON-FRI", zone = "Asia/Seoul")
    @Transactional
    public void recordOverdue() {
        LocalDateTime now = LocalDateTime.now(DateUtil.KST);

        for (RentalRecord rental : rentalHistoryService.getAllReturnRequiredRentals()) {
            if (rental.dueAt().isBefore(now)) {
                outboxWriter.record(new RentalOverdue(
                    rental.history().getMemberId(), rental.history().getId(), rental.itemName()));
            }
        }
    }

    private static LocalDateTime floorToCheckInterval(LocalDateTime time) {
        LocalDateTime minute = time.truncatedTo(ChronoUnit.MINUTES);
        return minute.minusMinutes(minute.getMinute() % DUE_SOON_CHECK_INTERVAL_MINUTES);
    }
}
