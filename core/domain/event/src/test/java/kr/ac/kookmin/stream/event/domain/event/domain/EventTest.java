package kr.ac.kookmin.stream.event.domain.event.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    private static Event event(RecruitType recruitType) {
        return Event.of(
            1L, "2026 소융대 개강 행사", "설명", "101호",
            LocalDateTime.of(2026, 9, 15, 18, 0), LocalDateTime.of(2026, 9, 15, 20, 0),
            APPLY_START_AT, APPLY_END_AT,
            recruitType, List.of(), CAPACITY, false, true, 1L, null
        );
    }

    private static Event firstComeEvent() {
        return event(RecruitType.FIRST_COME);
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
            Event openRecruit = event(RecruitType.OPEN);
            assertEquals(
                RecruitStatus.OPEN,
                openRecruit.calculateRecruitStatus(APPLY_START_AT.plusDays(1), CAPACITY + 100)
            );
        }
    }

    @Nested
    @DisplayName("마감까지 남은 일수")
    class DaysUntilDeadline {

        // 신청 마감은 APPLY_END_AT = 2026-09-10 18:00

        @Test
        @DisplayName("모집 중이면 마감일까지 남은 날짜 수를 센다")
        void countsRemainingDays() {
            Event event = firstComeEvent();

            assertEquals(2, event.daysUntilDeadline(
                LocalDateTime.of(2026, 9, 8, 10, 0), RecruitStatus.OPEN));
        }

        @Test
        @DisplayName("마감 당일이면 0이다")
        void deadlineToday() {
            Event event = firstComeEvent();

            assertEquals(0, event.daysUntilDeadline(
                LocalDateTime.of(2026, 9, 10, 9, 0), RecruitStatus.OPEN));
        }

        @Test
        @DisplayName("시각이 아니라 날짜로 세므로 19시간 뒤 마감이어도 하루 뒤면 1이다")
        void countsByDateNotByHours() {
            Event event = firstComeEvent();

            assertEquals(1, event.daysUntilDeadline(
                LocalDateTime.of(2026, 9, 9, 23, 0), RecruitStatus.OPEN));
        }

        @Test
        @DisplayName("모집 중이 아니면 내려보내지 않는다")
        void nullWhenNotOpen() {
            Event event = firstComeEvent();
            LocalDateTime now = LocalDateTime.of(2026, 9, 8, 10, 0);

            assertNull(event.daysUntilDeadline(now, RecruitStatus.BEFORE_OPEN));
            assertNull(event.daysUntilDeadline(now, RecruitStatus.CLOSED));
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
            assertFalse(event(RecruitType.OPEN).isCapacityFull(CAPACITY + 100));
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
        @DisplayName("정원이 남았으면 거짓이다")
        void notFull() {
            assertFalse(firstComeEvent().isClosedByCapacityOnly(APPLY_START_AT.plusDays(1), CAPACITY - 1));
        }
    }

    @Nested
    @DisplayName("운영진 등록")
    class Create {

        private EventCreateCommand command(RecruitType recruitType, Integer capacity) {
            return new EventCreateCommand(
                "체육대회", "학과 대항 체육대회", "종합운동장",
                EventPeriod.of(LocalDateTime.of(2026, 11, 1, 10, 0), LocalDateTime.of(2026, 11, 1, 18, 0)),
                EventPeriod.of(APPLY_START_AT, APPLY_END_AT), RecruitPolicy.of(recruitType, capacity, true),
                List.of(31L, 32L), List.of()
            );
        }

        @Test
        @DisplayName("입력값을 그대로 담고 작성자를 기록한다")
        void copiesCommand() {
            Event event = Event.create(7L, command(RecruitType.FIRST_COME, 100));

            assertNull(event.getId());
            assertEquals("체육대회", event.getTitle());
            assertEquals("학과 대항 체육대회", event.getDescription());
            assertEquals("종합운동장", event.getPlace());
            assertEquals(APPLY_START_AT, event.getApplyStartAt());
            assertEquals(APPLY_END_AT, event.getApplyEndAt());
            assertEquals(RecruitType.FIRST_COME, event.getRecruitType());
            assertEquals(100, event.getCapacity());
            assertTrue(event.isFeePaidOnly());
            assertEquals(List.of(31L, 32L), event.getImageIds());
            assertEquals(7L, event.getCreatedBy());
        }

        @Test
        @DisplayName("학생에게 바로 보이지 않도록 비공개로 시작한다")
        void startsUnpublished() {
            assertFalse(Event.create(7L, command(RecruitType.FIRST_COME, 100)).isPublished());
        }

        @Test
        @DisplayName("상시 모집은 입력한 정원과 상관없이 0으로 저장한다")
        void openRecruitStoresZeroCapacity() {
            assertEquals(0, Event.create(7L, command(RecruitType.OPEN, 50)).getCapacity());
            assertEquals(0, Event.create(7L, command(RecruitType.OPEN, null)).getCapacity());
        }
    }

    @Nested
    @DisplayName("운영진 수정")
    class Update {

        private EventUpdateCommand command(RecruitType recruitType, Integer capacity) {
            return new EventUpdateCommand(
                "체육대회(변경)", "장소가 바뀌었습니다", "미래관 대강당",
                EventPeriod.of(LocalDateTime.of(2026, 11, 2, 10, 0), LocalDateTime.of(2026, 11, 2, 18, 0)),
                EventPeriod.of(APPLY_START_AT.plusDays(1), APPLY_END_AT.plusDays(1)),
                RecruitPolicy.of(recruitType, capacity, true), List.of(40L), List.of(), LocalDateTime.now()
            );
        }

        @Test
        @DisplayName("내용을 통째로 바꾼다")
        void replacesContent() {
            Event event = firstComeEvent();

            event.update(command(RecruitType.FIRST_COME, 50));

            assertEquals("체육대회(변경)", event.getTitle());
            assertEquals("장소가 바뀌었습니다", event.getDescription());
            assertEquals("미래관 대강당", event.getPlace());
            assertEquals(LocalDateTime.of(2026, 11, 2, 10, 0), event.getEventStartAt());
            assertEquals(LocalDateTime.of(2026, 11, 2, 18, 0), event.getEventEndAt());
            assertEquals(APPLY_START_AT.plusDays(1), event.getApplyStartAt());
            assertEquals(APPLY_END_AT.plusDays(1), event.getApplyEndAt());
            assertEquals(50, event.getCapacity());
            assertTrue(event.isFeePaidOnly());
            assertEquals(List.of(40L), event.getImageIds());
        }

        @Test
        @DisplayName("공개 여부와 작성자는 그대로 둔다")
        void keepsPublishedAndCreator() {
            Event event = firstComeEvent();

            event.update(command(RecruitType.FIRST_COME, 50));

            assertTrue(event.isPublished());
            assertEquals(1L, event.getCreatedBy());
            assertEquals(1L, event.getId());
        }

        @Test
        @DisplayName("상시 모집으로 바꾸면 정원을 0으로 둔다")
        void switchToOpenRecruitClearsCapacity() {
            Event event = firstComeEvent();

            event.update(command(RecruitType.OPEN, 50));

            assertEquals(RecruitType.OPEN, event.getRecruitType());
            assertEquals(0, event.getCapacity());
        }
    }

    @Nested
    @DisplayName("운영진 화면의 정원")
    class CapacityLimit {

        @Test
        @DisplayName("선착순이면 정원을 돌려준다")
        void firstComeReturnsCapacity() {
            assertEquals(CAPACITY, firstComeEvent().capacityLimit());
        }

        @Test
        @DisplayName("상시 모집은 인원 제한이 없어 null이다")
        void openRecruitReturnsNull() {
            assertNull(event(RecruitType.OPEN).capacityLimit());
        }
    }

    @Nested
    @DisplayName("공개 여부 변경")
    class ChangePublished {

        @Test
        @DisplayName("공개 여부만 바꾼다")
        void changesOnlyPublished() {
            Event event = firstComeEvent();

            event.changePublished(false);

            assertFalse(event.isPublished());
            assertEquals("2026 소융대 개강 행사", event.getTitle());
        }
    }
}
