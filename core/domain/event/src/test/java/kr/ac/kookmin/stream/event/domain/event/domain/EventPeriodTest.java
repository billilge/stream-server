package kr.ac.kookmin.stream.event.domain.event.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.common.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventPeriodTest {

    private static final LocalDateTime START_AT = LocalDateTime.of(2026, 11, 1, 10, 0);

    private static void assertRejected(Runnable creation) {
        BusinessException e = assertThrows(BusinessException.class, creation::run);
        assertEquals(EventErrorCode.EVENT_INVALID_PERIOD, e.getErrorCode());
    }

    @Test
    @DisplayName("종료가 시작보다 빠르면 거부한다")
    void endsBeforeStart() {
        assertRejected(() -> EventPeriod.of(START_AT, START_AT.minusMinutes(1)));
    }

    @Test
    @DisplayName("시작과 종료가 같으면 거부한다")
    void endsAtStart() {
        assertRejected(() -> EventPeriod.of(START_AT, START_AT));
    }

    @Test
    @DisplayName("종료가 시작보다 늦으면 기간을 만든다")
    void endsAfterStart() {
        assertDoesNotThrow(() -> EventPeriod.of(START_AT, START_AT.plusSeconds(1)));
    }
}
