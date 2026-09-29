package kr.ac.kookmin.stream.event.domain.locker.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LockerApplicationStatusTest {

    private static final LockerPeriod PERIOD = LockerPeriod.of(
        1L,
        "2026-2학기",
        LocalDateTime.of(2026, 8, 20, 10, 0),
        LocalDateTime.of(2026, 8, 25, 18, 0),
        LocalDate.of(2026, 9, 7),
        LocalDate.of(2026, 12, 15),
        true
    );

    @ParameterizedTest
    @DisplayName("사용 종료일 당일까지는 배정 상태이고, 다음 날부터 이용 종료다")
    @CsvSource({
        "2026-09-01, ASSIGNED",   // 사용 시작 전
        "2026-09-07, ASSIGNED",   // 사용 시작일
        "2026-12-15, ASSIGNED",   // 사용 종료일 당일
        "2026-12-16, EXPIRED",    // 사용 종료 다음 날
        "2027-03-02, EXPIRED"
    })
    void byUsageEndDate(LocalDate today, LockerApplicationStatus expected) {
        assertEquals(expected, LockerApplicationStatus.from(PERIOD, today));
    }
}
