package kr.ac.kookmin.stream.event.domain.event.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import kr.ac.kookmin.stream.common.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecruitPolicyTest {

    private static void assertRejected(Runnable creation) {
        BusinessException e = assertThrows(BusinessException.class, creation::run);
        assertEquals(EventErrorCode.EVENT_INVALID_CAPACITY, e.getErrorCode());
    }

    @Test
    @DisplayName("선착순인데 정원이 없으면 거부한다")
    void firstComeWithoutCapacity() {
        assertRejected(() -> RecruitPolicy.of(RecruitType.FIRST_COME, null, false));
    }

    @Test
    @DisplayName("선착순인데 정원이 0명이면 거부한다")
    void firstComeWithZeroCapacity() {
        assertRejected(() -> RecruitPolicy.of(RecruitType.FIRST_COME, 0, false));
    }

    @Test
    @DisplayName("선착순 정원은 1명부터 허용한다")
    void firstComeWithOneCapacity() {
        assertDoesNotThrow(() -> RecruitPolicy.of(RecruitType.FIRST_COME, 1, false));
    }

    @Test
    @DisplayName("상시 모집은 정원을 보지 않는다")
    void openRecruitIgnoresCapacity() {
        assertDoesNotThrow(() -> RecruitPolicy.of(RecruitType.OPEN, null, false));
        assertDoesNotThrow(() -> RecruitPolicy.of(RecruitType.OPEN, -1, false));
    }
}
