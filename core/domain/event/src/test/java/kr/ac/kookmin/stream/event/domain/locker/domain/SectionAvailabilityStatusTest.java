package kr.ac.kookmin.stream.event.domain.locker.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SectionAvailabilityStatusTest {

    @ParameterizedTest
    @DisplayName("명세의 예시가 모두 같은 상태로 판정된다")
    @CsvSource({
        "24, 34, PLENTY",       // 70.6%
        "6,   6, PLENTY",       // 100%
        "24, 60, NORMAL",       // 40%
        "2,  40, ALMOST_FULL",  // 5%
        "0, 100, FULL"          // 0%
    })
    void specExamples(int available, int total, SectionAvailabilityStatus expected) {
        assertEquals(expected, SectionAvailabilityStatus.from(available, total));
    }

    @ParameterizedTest
    @DisplayName("잔여율 경계값은 더 여유로운 쪽으로 판정된다")
    @CsvSource({
        "50, 100, PLENTY",      // 정확히 50%
        "49, 100, NORMAL",      // 50% 바로 아래
        "20, 100, NORMAL",      // 정확히 20%
        "19, 100, ALMOST_FULL"  // 20% 바로 아래
    })
    void boundaries(int available, int total, SectionAvailabilityStatus expected) {
        assertEquals(expected, SectionAvailabilityStatus.from(available, total));
    }

    @Test
    @DisplayName("선택 가능 수가 0이면 전체 수와 무관하게 마감이다")
    void zeroAvailableIsFull() {
        assertEquals(SectionAvailabilityStatus.FULL, SectionAvailabilityStatus.from(0, 100));
        assertEquals(SectionAvailabilityStatus.FULL, SectionAvailabilityStatus.from(0, 1));
    }

    @Test
    @DisplayName("사물함이 없는 구역은 0으로 나누지 않고 마감으로 본다")
    void emptySection() {
        assertEquals(SectionAvailabilityStatus.FULL, SectionAvailabilityStatus.from(0, 0));
    }
}
