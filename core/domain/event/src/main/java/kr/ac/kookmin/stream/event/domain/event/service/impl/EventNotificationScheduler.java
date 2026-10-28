package kr.ac.kookmin.stream.event.domain.event.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.DateUtil;
import kr.ac.kookmin.stream.common.OutboxWriter;
import kr.ac.kookmin.stream.common.event.EventApplyClosingTomorrow;
import kr.ac.kookmin.stream.common.event.EventStartingTomorrow;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.event.domain.event.repository.EventApplicationRepository;
import kr.ac.kookmin.stream.event.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 행사 예약 알림을 아웃박스에 기록한다. 매일 10시에 내일 시작하는 행사와 내일 신청이 마감되는 행사를 고른다.
 * <p>
 * 발송 기록을 따로 남기지 않고 "날짜가 내일"인 대상만 고르므로, 서버가 그 시각에 내려가 있었으면 그날 알림은 건너뛴다.
 */
@Component
@RequiredArgsConstructor
class EventNotificationScheduler {

    private final EventRepository eventRepository;
    private final EventApplicationRepository eventApplicationRepository;
    private final OutboxWriter outboxWriter;

    @Scheduled(cron = "0 0 10 * * *", zone = "Asia/Seoul")
    @Transactional
    public void recordDailyNotifications() {
        LocalDateTime now = LocalDateTime.now(DateUtil.KST);
        LocalDateTime tomorrow = now.toLocalDate().plusDays(1).atStartOfDay();
        LocalDateTime dayAfterTomorrow = tomorrow.plusDays(1);

        recordStartingTomorrow(tomorrow, dayAfterTomorrow);
        recordApplyClosingTomorrow(now, tomorrow, dayAfterTomorrow);
    }

    // 신청자가 없으면 받을 사람이 없어 기록하지 않는다
    private void recordStartingTomorrow(LocalDateTime from, LocalDateTime to) {
        for (Event event : eventRepository.findPublishedStartingBetween(from, to)) {
            List<Long> applicantIds = eventApplicationRepository.findAppliedMemberIds(event.getId());
            if (!applicantIds.isEmpty()) {
                outboxWriter.record(new EventStartingTomorrow(event.getId(), event.getTitle(), applicantIds));
            }
        }
    }

    // 지금 신청할 수 있는 행사만 알린다. 정원 마감된 행사는 신청할 수 없어 뺀다
    private void recordApplyClosingTomorrow(LocalDateTime now, LocalDateTime from, LocalDateTime to) {
        for (Event event : eventRepository.findPublishedApplyEndingBetween(from, to)) {
            List<Long> applicantIds = eventApplicationRepository.findAppliedMemberIds(event.getId());
            if (event.calculateRecruitStatus(now, applicantIds.size()) == RecruitStatus.OPEN) {
                outboxWriter.record(new EventApplyClosingTomorrow(event.getId(), event.getTitle(), applicantIds));
            }
        }
    }
}
