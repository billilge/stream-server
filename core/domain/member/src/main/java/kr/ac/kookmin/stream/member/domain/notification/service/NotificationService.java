package kr.ac.kookmin.stream.member.domain.notification.service;

import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.member.domain.notification.domain.Notification;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationCursor;

public interface NotificationService {
    CursorSliceResult<Notification> getNotifications(Long memberId, NotificationCursor cursor, int size);

    void markRead(Long memberId, Long notificationId);

    boolean hasUnread(Long memberId);
}
