package kr.ac.kookmin.stream.event.domain.event.domain;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.Cursor;

/**
 * 행사 목록의 keyset 커서. 정렬 기준(행사 시작 일시 오름차순 + eventId 오름차순)과 짝을 이룬다.
 */
public record EventCursor(LocalDateTime eventStartAt, Long eventId) implements Cursor {

    private static final int PART_COUNT = 2;

    public static EventCursor of(Event event) {
        return new EventCursor(event.getEventStartAt(), event.getId());
    }

    public static EventCursor of(EventSummary summary) {
        return new EventCursor(summary.eventStartAt(), summary.eventId());
    }

    public static EventCursor from(String raw) {
        try {
            List<String> parts = Cursor.parseParts(raw, PART_COUNT, EventErrorCode.EVENT_INVALID_CURSOR);
            return new EventCursor(LocalDateTime.parse(parts.get(0)), Long.valueOf(parts.get(1)));
        } catch (RuntimeException e) {
            throw new BusinessException(EventErrorCode.EVENT_INVALID_CURSOR);
        }
    }

    @Override
    public List<String> toParts() {
        return List.of(eventStartAt.toString(), String.valueOf(eventId));
    }
}
