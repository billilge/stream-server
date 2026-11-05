package kr.ac.kookmin.stream.member.domain.notification.domain;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.Cursor;

public record NotificationCursor(LocalDateTime createdAt, Long id) implements Cursor {

    private static final String JOIN = "|";
    private static final int PART_COUNT = 2;

    public static NotificationCursor of(Notification notification) {
        return new NotificationCursor(notification.getCreatedAt(), notification.getId());
    }

    public static NotificationCursor from(String raw) {
        try {
            List<String> parts = Cursor.parseParts(raw, PART_COUNT, NotificationErrorCode.NOTIFICATION_INVALID_CURSOR);
            LocalDateTime createdAt = LocalDateTime.parse(parts.get(0));
            Long id = Long.valueOf(parts.get(1));
            return new NotificationCursor(createdAt, id);
        } catch (RuntimeException e) {
            throw new BusinessException(NotificationErrorCode.NOTIFICATION_INVALID_CURSOR);
        }
    }

    @Override
    public String format() {
        return createdAt + JOIN + id;
    }
}
