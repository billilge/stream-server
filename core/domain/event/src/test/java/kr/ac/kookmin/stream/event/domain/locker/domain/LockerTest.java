package kr.ac.kookmin.stream.event.domain.locker.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 선택 가능 판정은 구역 목록의 선택 가능 수와 구역 상세의 선택 가능 여부가 함께 쓰는 규칙이라 여기서 덮는다.
 */
class LockerTest {

    private static Locker locker(LockerStatus status) {
        return Locker.of(1L, 10L, "A-1", 1, 1, 1, status);
    }

    @Test
    @DisplayName("사용 가능하고 신청되지 않았으면 선택할 수 있다")
    void selectableWhenAvailableAndNotApplied() {
        assertTrue(locker(LockerStatus.AVAILABLE).isSelectable(false));
    }

    @Test
    @DisplayName("사용 가능해도 이미 신청됐으면 선택할 수 없다")
    void notSelectableWhenApplied() {
        assertFalse(locker(LockerStatus.AVAILABLE).isSelectable(true));
    }

    @Test
    @DisplayName("사용 중지된 사물함은 신청되지 않았어도 선택할 수 없다")
    void notSelectableWhenDisabled() {
        assertFalse(locker(LockerStatus.DISABLED).isSelectable(false));
    }

    @Test
    @DisplayName("사용 중지됐고 신청까지 됐으면 선택할 수 없다")
    void notSelectableWhenDisabledAndApplied() {
        assertFalse(locker(LockerStatus.DISABLED).isSelectable(true));
    }
}
