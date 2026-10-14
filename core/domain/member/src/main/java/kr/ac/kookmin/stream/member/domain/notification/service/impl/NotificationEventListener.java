package kr.ac.kookmin.stream.member.domain.notification.service.impl;

import java.util.List;
import kr.ac.kookmin.stream.common.event.EventApplicationCanceled;
import kr.ac.kookmin.stream.common.event.EventApplied;
import kr.ac.kookmin.stream.common.event.LockerAssigned;
import kr.ac.kookmin.stream.common.event.NoticeCreated;
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
}
