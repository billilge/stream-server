package kr.ac.kookmin.stream.member.domain.notification.service;

import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.member.domain.notification.domain.Notification;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationCursor;

/** 운영진 공용 알림함(member_id가 없는 행). {@link NotificationService}와 분리하고 서로 호출하지 않는다. */
public interface AdminNotificationService {
    CursorSliceResult<Notification> getNotifications(NotificationCursor cursor, int size);

    void markRead(Long notificationId);

    boolean hasUnread();
}
