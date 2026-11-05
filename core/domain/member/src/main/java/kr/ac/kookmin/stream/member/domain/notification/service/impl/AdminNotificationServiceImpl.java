package kr.ac.kookmin.stream.member.domain.notification.service.impl;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.member.domain.notification.domain.Notification;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationCursor;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationErrorCode;
import kr.ac.kookmin.stream.member.domain.notification.repository.NotificationRepository;
import kr.ac.kookmin.stream.member.domain.notification.service.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class AdminNotificationServiceImpl implements AdminNotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public CursorSliceResult<Notification> getNotifications(NotificationCursor cursor, int size) {
        return notificationRepository.findAdminSlice(cursor, size);
    }

    // 운영진 전체가 공유하는 행이라 누가 읽어도 전체가 읽음으로 본다
    @Override
    @Transactional
    public void markRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .filter(found -> found.getMemberId() == null)
            .orElseThrow(() -> new BusinessException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
        if (notification.isRead()) {
            return;
        }
        notification.markRead();
        notificationRepository.save(notification);
    }

    @Override
    public boolean hasUnread() {
        return notificationRepository.existsAdminUnread();
    }
}
