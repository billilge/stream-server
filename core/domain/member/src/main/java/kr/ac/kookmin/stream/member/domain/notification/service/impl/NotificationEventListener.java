package kr.ac.kookmin.stream.member.domain.notification.service.impl;

import java.util.List;
import kr.ac.kookmin.stream.common.event.EventApplicationCanceled;
import kr.ac.kookmin.stream.common.event.EventApplied;
import kr.ac.kookmin.stream.common.event.EventApplyClosingTomorrow;
import kr.ac.kookmin.stream.common.event.EventStartingTomorrow;
import kr.ac.kookmin.stream.common.event.LockerApplyClosingTomorrow;
import kr.ac.kookmin.stream.common.event.LockerAssigned;
import kr.ac.kookmin.stream.common.event.LockerUsageEndingSoon;
import kr.ac.kookmin.stream.common.event.NoticeCreated;
import kr.ac.kookmin.stream.common.event.RentalDueSoon;
import kr.ac.kookmin.stream.common.event.RentalOverdue;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 다른 도메인의 이벤트를 알림으로 바꾼다. 아웃박스 릴레이가 트랜잭션 밖에서 발행하므로 {@code @EventListener}로 받는다.
 * 예외를 던지면 릴레이가 같은 이벤트를 다시 발행한다.
 */
@Component
@RequiredArgsConstructor
class NotificationEventListener {

    private final NotificationDispatcher notificationDispatcher;

    @EventListener
    public void on(EventApplied event) {
        notificationDispatcher.dispatch(
            NotificationType.EVENT_APPLIED, List.of(event.eventTitle()), event.eventId(), List.of(event.memberId()));
    }

    @EventListener
    public void on(EventApplicationCanceled event) {
        notificationDispatcher.dispatch(
            NotificationType.EVENT_APPLICATION_CANCELED, List.of(event.eventTitle()), event.eventId(), List.of(event.memberId()));
    }

    @EventListener
    public void on(LockerAssigned event) {
        notificationDispatcher.dispatch(
            NotificationType.LOCKER_ASSIGNED, List.of(), event.lockerPeriodId(), List.of(event.memberId()));
    }

    // 공지는 활성 회원 전체에게 간다
    @EventListener
    public void on(NoticeCreated event) {
        notificationDispatcher.dispatchToAll(NotificationType.NOTICE_CREATED, List.of(), event.noticeId(), List.of());
    }

    @EventListener
    public void on(EventStartingTomorrow event) {
        notificationDispatcher.dispatch(
            NotificationType.EVENT_STARTING_TOMORROW, List.of(event.eventTitle()), event.eventId(), event.applicantIds());
    }

    // 마감 임박은 아직 신청하지 않은 회원에게 간다
    @EventListener
    public void on(EventApplyClosingTomorrow event) {
        notificationDispatcher.dispatchToAll(
            NotificationType.EVENT_APPLY_CLOSING, List.of(event.eventTitle()), event.eventId(), event.applicantIds());
    }

    @EventListener
    public void on(LockerApplyClosingTomorrow event) {
        notificationDispatcher.dispatchToAll(
            NotificationType.LOCKER_APPLY_CLOSING, List.of(), event.lockerPeriodId(), event.applicantIds());
    }

    @EventListener
    public void on(LockerUsageEndingSoon event) {
        notificationDispatcher.dispatch(
            NotificationType.LOCKER_USAGE_ENDING, List.of(), event.lockerPeriodId(), event.applicantIds());
    }

    @EventListener
    public void on(RentalDueSoon event) {
        notificationDispatcher.dispatch(
            NotificationType.RENTAL_DUE_SOON, List.of(event.itemName()), event.rentalHistoryId(), List.of(event.memberId()));
    }

    @EventListener
    public void on(RentalOverdue event) {
        notificationDispatcher.dispatch(
            NotificationType.RENTAL_OVERDUE, List.of(event.itemName()), event.rentalHistoryId(), List.of(event.memberId()));
    }
}
