package kr.ac.kookmin.stream.event.domain.event.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventApplicationTest {

    private static final LocalDateTime APPLIED_AT = LocalDateTime.of(2026, 8, 30, 14, 3, 21);
    private static final LocalDateTime CANCELED_AT = LocalDateTime.of(2026, 8, 30, 15, 0);

    private static EventApplication applied() {
        return EventApplication.of(102L, 15L, 1L, EventApplicationStatus.APPLIED, APPLIED_AT, null);
    }

    @Test
    @DisplayName("신청 직후 상태는 APPLIED이고 취소 시각은 비어 있다")
    void create() {
        EventApplication application = EventApplication.create(15L, 1L, APPLIED_AT);

        assertEquals(EventApplicationStatus.APPLIED, application.getStatus());
        assertNull(application.getCanceledAt());
        assertFalse(application.isCanceled());
    }

    @Test
    @DisplayName("취소하면 상태와 취소 시각만 바뀌고 나머지 기록은 그대로다")
    void cancel() {
        EventApplication application = applied();

        application.cancel(CANCELED_AT);

        assertEquals(EventApplicationStatus.CANCELED, application.getStatus());
        assertEquals(CANCELED_AT, application.getCanceledAt());
        assertTrue(application.isCanceled());
        assertEquals(102L, application.getId());
        assertEquals(15L, application.getEventId());
        assertEquals(1L, application.getMemberId());
        assertEquals(APPLIED_AT, application.getAppliedAt());
    }
}
