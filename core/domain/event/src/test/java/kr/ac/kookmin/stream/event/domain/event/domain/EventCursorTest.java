package kr.ac.kookmin.stream.event.domain.event.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
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
        EventCursor cursor = new EventCursor(RecruitStatus.OPEN, EVENT_START_AT, 42L);

        assertEquals(cursor, EventCursor.from(cursor.format()));
    }

    @Test
    @DisplayName("모집 상태 구간마다 그 구간의 정렬 키를 딴다")
    void sortAtPerRecruitStatus() {
        LocalDateTime applyStartAt = EVENT_START_AT.minusDays(7);
        LocalDateTime applyEndAt = EVENT_START_AT.minusDays(1);

        assertEquals(new EventCursor(RecruitStatus.OPEN, applyEndAt, 42L),
            EventCursor.of(summary(RecruitStatus.OPEN, applyStartAt, applyEndAt)));
        assertEquals(new EventCursor(RecruitStatus.BEFORE_OPEN, applyStartAt, 42L),
            EventCursor.of(summary(RecruitStatus.BEFORE_OPEN, applyStartAt, applyEndAt)));
        assertEquals(new EventCursor(RecruitStatus.CLOSED, EVENT_START_AT, 42L),
            EventCursor.of(summary(RecruitStatus.CLOSED, applyStartAt, applyEndAt)));
    }

    @ParameterizedTest
    @DisplayName("형식이 어긋나면 커서 오류를 던진다")
    @ValueSource(strings = {
        "",
        "OPEN|2026-09-12T10:00",
        "OPEN|2026-09-12T10:00|42|extra",
        "UNKNOWN|2026-09-12T10:00|42",
        "OPEN|not-a-date|42",
        "OPEN|2026-09-12T10:00|not-a-number",
        "|2026-09-12T10:00|42"
    })
    void invalidFormat(String raw) {
        BusinessException e = assertThrows(BusinessException.class, () -> EventCursor.from(raw));

        assertEquals(EventErrorCode.EVENT_INVALID_CURSOR, e.getErrorCode());
    }

    private static EventSummary summary(
        RecruitStatus recruitStatus, LocalDateTime applyStartAt, LocalDateTime applyEndAt) {
        return new EventSummary(42L, "행사", EVENT_START_AT, null, applyStartAt, applyEndAt, recruitStatus, null);
    }
}
