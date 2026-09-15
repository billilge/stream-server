package kr.ac.kookmin.stream.event.domain.event.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EventCursorTest {

    private static final LocalDateTime EVENT_START_AT = LocalDateTime.of(2026, 9, 12, 10, 0);

    @Test
    @DisplayName("문자열로 바꿨다가 되돌리면 같은 커서다")
    void roundTrip() {
        EventCursor cursor = new EventCursor(EVENT_START_AT, 42L);

        assertEquals(cursor, EventCursor.from(cursor.format()));
    }

    @Test
    @DisplayName("행사로부터 정렬 키를 그대로 딴다")
    void fromEvent() {
        Event event = Event.of(
            42L, "행사", "설명", "전교생", "학생회관",
            EVENT_START_AT, EVENT_START_AT.plusHours(2), EVENT_START_AT.minusDays(7), EVENT_START_AT.minusDays(1),
            RecruitType.OPEN, List.of(), 0, RecruitStatus.OPEN, true, 100L
        );

        assertEquals(new EventCursor(EVENT_START_AT, 42L), EventCursor.of(event));
    }

    @ParameterizedTest
    @DisplayName("형식이 어긋나면 커서 오류를 던진다")
    @ValueSource(strings = {
        "",
        "2026-09-12T10:00",
        "2026-09-12T10:00|42|extra",
        "not-a-date|42",
        "2026-09-12T10:00|not-a-number",
        "|42"
    })
    void invalidFormat(String raw) {
        BusinessException e = assertThrows(BusinessException.class, () -> EventCursor.from(raw));

        assertEquals(EventErrorCode.EVENT_INVALID_CURSOR, e.getErrorCode());
    }
}
