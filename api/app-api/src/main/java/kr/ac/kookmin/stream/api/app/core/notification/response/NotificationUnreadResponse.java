package kr.ac.kookmin.stream.api.app.core.notification.response;

/**
 * @param hasUnread 내 알림 중 안 읽은 알림이 하나라도 있으면 true
 */
public record NotificationUnreadResponse(
    boolean hasUnread
) {}
