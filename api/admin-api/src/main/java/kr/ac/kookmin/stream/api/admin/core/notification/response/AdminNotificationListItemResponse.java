package kr.ac.kookmin.stream.api.admin.core.notification.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.member.domain.notification.domain.Notification;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationType;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;

public record AdminNotificationListItemResponse(
    Long notificationId,
    NotificationType type,
    String title,
    String body,
    Long targetId,
    boolean read,
    LocalDateTime createdAt,
    // targetId가 가리키는 대여 기록의 지금 상태. 대여 알림이 아니거나 대여 기록이 지워졌으면 null
    RentalStatus rentalStatus
) {

    public static AdminNotificationListItemResponse from(Notification notification, RentalStatus rentalStatus) {
        return new AdminNotificationListItemResponse(
            notification.getId(),
            notification.getType(),
            notification.getType().formatTitle(notification.getFormatValueList()),
            notification.getType().formatBody(notification.getFormatValueList()),
            notification.getTargetId(),
            notification.isRead(),
            notification.getCreatedAt(),
            rentalStatus
        );
    }
}
