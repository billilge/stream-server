package kr.ac.kookmin.stream.api.app.core.notification.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import kr.ac.kookmin.stream.api.common.CursorCodec;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationCursor;

public record NotificationListParams(
    String cursor,

    @Min(value = 1, message = "조회 개수는 1 이상 100 이하여야 합니다.")
    @Max(value = 100, message = "조회 개수는 1 이상 100 이하여야 합니다.")
    Integer size
) {

    private static final int DEFAULT_SIZE = 20;

    public NotificationCursor toCursor() {
        return cursor == null ? null : NotificationCursor.from(CursorCodec.decode(cursor));
    }

    public int sizeOrDefault() {
        return size == null ? DEFAULT_SIZE : size;
    }
}
