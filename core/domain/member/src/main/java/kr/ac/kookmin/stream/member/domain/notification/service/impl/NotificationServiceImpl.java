package kr.ac.kookmin.stream.member.domain.notification.service.impl;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.member.domain.notification.domain.Notification;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationCursor;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationErrorCode;
import kr.ac.kookmin.stream.member.domain.notification.repository.NotificationRepository;
import kr.ac.kookmin.stream.member.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public CursorSliceResult<Notification> getNotifications(Long memberId, NotificationCursor cursor, int size) {
        return notificationRepository.findSliceByMemberId(memberId, cursor, size);
    }

    @Override
    @Transactional
    public void markRead(Long memberId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .filter(found -> memberId.equals(found.getMemberId()))
            .orElseThrow(() -> new BusinessException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
        if (notification.isRead()) {
            return;
        }
        notification.markRead();
        notificationRepository.save(notification);
    }

    @Override
    public boolean hasUnread(Long memberId) {
        return notificationRepository.existsUnreadByMemberId(memberId);
    }
}
