package kr.ac.kookmin.stream.api.app.core.notification.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.member.domain.notification.domain.Notification;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationType;

public record NotificationListItemResponse(
    Long notificationId,
    NotificationType type,
    String title,
    String body,
    Long targetId,
    boolean read,
    LocalDateTime createdAt
) {

    public static NotificationListItemResponse from(Notification notification) {
        return new NotificationListItemResponse(
            notification.getId(),
            notification.getType(),
            notification.getType().formatTitle(notification.getFormatValueList()),
            notification.getType().formatBody(notification.getFormatValueList()),
            notification.getTargetId(),
            notification.isRead(),
            notification.getCreatedAt()
        );
    }
}
