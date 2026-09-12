package kr.ac.kookmin.stream.event.domain.event.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EventTest {

    private static final LocalDateTime APPLY_START_AT = LocalDateTime.of(2026, 9, 1, 10, 0);
    private static final LocalDateTime APPLY_END_AT = LocalDateTime.of(2026, 9, 10, 18, 0);
    private static final int CAPACITY = 30;

    private static Event event(RecruitType recruitType, RecruitStatus recruitStatus) {
        return Event.of(
            1L, "2026 소융대 개강 행사", "설명", "소융대 재학생", "101호",
            LocalDateTime.of(2026, 9, 15, 18, 0), LocalDateTime.of(2026, 9, 15, 20, 0),
            APPLY_START_AT, APPLY_END_AT,
            recruitType, List.of(), CAPACITY, recruitStatus, true, 1L
        );
    }

    private static Event firstComeEvent() {
        return event(RecruitType.FIRST_COME, RecruitStatus.OPEN);
    }

    @Nested
    @DisplayName("모집 상태 계산")
    class CalculateRecruitStatus {

        @Test
        @DisplayName("신청 기간 안이고 정원이 남았으면 OPEN이다")
        void openWithinPeriod() {
            assertEquals(
                RecruitStatus.OPEN,
                firstComeEvent().calculateRecruitStatus(APPLY_START_AT.plusDays(1), CAPACITY - 1)
            );
        }

        @Test
        @DisplayName("신청 시작 전이면 BEFORE_OPEN이다")
        void beforeApplyStart() {
            assertEquals(
                RecruitStatus.BEFORE_OPEN,
                firstComeEvent().calculateRecruitStatus(APPLY_START_AT.minusSeconds(1), 0)
            );
        }

        @Test
        @DisplayName("신청 종료 후면 CLOSED다")
        void afterApplyEnd() {
            assertEquals(
                RecruitStatus.CLOSED,
                firstComeEvent().calculateRecruitStatus(APPLY_END_AT.plusSeconds(1), 0)
            );
        }

        @Test
        @DisplayName("신청 시작·종료 시각 정각은 모집 중으로 본다")
        void boundaryInstantsAreOpen() {
            assertEquals(RecruitStatus.OPEN, firstComeEvent().calculateRecruitStatus(APPLY_START_AT, 0));
            assertEquals(RecruitStatus.OPEN, firstComeEvent().calculateRecruitStatus(APPLY_END_AT, 0));
        }

        @Test
        @DisplayName("운영진이 강제 마감했으면 기간 안이어도 CLOSED다")
        void forceClosedBeatsPeriod() {
            Event forceClosed = event(RecruitType.FIRST_COME, RecruitStatus.CLOSED);
            assertEquals(
                RecruitStatus.CLOSED,
                forceClosed.calculateRecruitStatus(APPLY_START_AT.plusDays(1), 0)
            );
        }

        @Test
        @DisplayName("선착순 모집은 정원이 차면 CLOSED다")
        void firstComeClosesWhenFull() {
            assertEquals(
                RecruitStatus.CLOSED,
                firstComeEvent().calculateRecruitStatus(APPLY_START_AT.plusDays(1), CAPACITY)
            );
        }

        @Test
        @DisplayName("상시 모집은 정원을 넘겨도 OPEN이다")
        void openRecruitIgnoresCapacity() {
            Event openRecruit = event(RecruitType.OPEN, RecruitStatus.OPEN);
            assertEquals(
                RecruitStatus.OPEN,
                openRecruit.calculateRecruitStatus(APPLY_START_AT.plusDays(1), CAPACITY + 100)
            );
        }
    }

    @Nested
    @DisplayName("정원 마감 여부")
    class CapacityFull {

        @Test
        @DisplayName("선착순 모집은 신청자가 정원 이상이면 마감이다")
        void firstComeIsFull() {
            assertTrue(firstComeEvent().isCapacityFull(CAPACITY));
            assertFalse(firstComeEvent().isCapacityFull(CAPACITY - 1));
        }

        @Test
        @DisplayName("상시 모집은 정원 제한이 없다")
        void openRecruitIsNeverFull() {
            assertFalse(event(RecruitType.OPEN, RecruitStatus.OPEN).isCapacityFull(CAPACITY + 100));
        }
    }

    @Nested
    @DisplayName("정원이 유일한 마감 사유인지")
    class ClosedByCapacityOnly {

        @Test
        @DisplayName("기간 안에서 정원만 찼으면 참이다")
        void capacityIsOnlyReason() {
            assertTrue(firstComeEvent().isClosedByCapacityOnly(APPLY_START_AT.plusDays(1), CAPACITY));
        }

        @Test
        @DisplayName("신청 기간이 끝났으면 정원이 차도 거짓이다")
        void periodEndedTakesPrecedence() {
            assertFalse(firstComeEvent().isClosedByCapacityOnly(APPLY_END_AT.plusSeconds(1), CAPACITY));
        }

        @Test
        @DisplayName("강제 마감이면 정원이 차도 거짓이다")
        void forceClosedTakesPrecedence() {
            Event forceClosed = event(RecruitType.FIRST_COME, RecruitStatus.CLOSED);
            assertFalse(forceClosed.isClosedByCapacityOnly(APPLY_START_AT.plusDays(1), CAPACITY));
        }

        @Test
        @DisplayName("정원이 남았으면 거짓이다")
        void notFull() {
            assertFalse(firstComeEvent().isClosedByCapacityOnly(APPLY_START_AT.plusDays(1), CAPACITY - 1));
        }
    }
}
