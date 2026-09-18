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

class EventApplicationCursorTest {

    private static final LocalDateTime APPLIED_AT = LocalDateTime.of(2026, 8, 30, 14, 3, 21);

    @Test
    @DisplayName("문자열로 바꿨다가 되돌리면 같은 커서다")
    void roundTrip() {
        EventApplicationCursor cursor = new EventApplicationCursor(APPLIED_AT, 102L);

        assertEquals(cursor, EventApplicationCursor.from(cursor.format()));
    }

    @Test
    @DisplayName("신청 내역으로부터 정렬 키를 그대로 딴다")
    void fromSummary() {
        Event event = Event.of(
            15L, "행사", "설명", "전교생", "학생회관",
            APPLIED_AT.plusDays(30), APPLIED_AT.plusDays(31), APPLIED_AT.minusDays(7), APPLIED_AT.plusDays(3),
            RecruitType.OPEN, List.of(), 0, RecruitStatus.OPEN, true, 100L
        );
        EventApplication application = EventApplication.of(
            102L, 15L, 1L, EventApplicationStatus.APPLIED, APPLIED_AT, null);

        EventApplicationSummary summary = EventApplicationSummary.of(application, event);

        assertEquals(new EventApplicationCursor(APPLIED_AT, 102L), EventApplicationCursor.of(summary));
    }

    @ParameterizedTest
    @DisplayName("형식이 어긋나면 커서 오류를 던진다")
    @ValueSource(strings = {
        "",
        "2026-08-30T14:03:21",
        "2026-08-30T14:03:21|102|extra",
        "not-a-date|102",
        "2026-08-30T14:03:21|not-a-number",
        "|102"
    })
    void invalidFormat(String raw) {
        BusinessException e = assertThrows(
            BusinessException.class, () -> EventApplicationCursor.from(raw));

        assertEquals(EventErrorCode.EVENT_INVALID_CURSOR, e.getErrorCode());
    }
}
