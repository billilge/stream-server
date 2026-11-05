package kr.ac.kookmin.stream.api.admin.core.notification.response;

/**
 * @param hasUnread 운영진 공용 알림 중 안 읽은 알림이 하나라도 있으면 true
 */
public record AdminNotificationUnreadResponse(
    boolean hasUnread
) {}
