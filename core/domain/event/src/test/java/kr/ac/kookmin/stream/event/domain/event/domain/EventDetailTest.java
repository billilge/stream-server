package kr.ac.kookmin.stream.event.domain.event.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EventDetailTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 12, 10, 0);

    private static Event event(RecruitType recruitType, RecruitStatus recruitStatus, List<Long> imageIds) {
        return Event.of(
            15L, "2026 소융대 개강 행사", "소융대 재학생을 위한 개강 행사입니다.", "소융대 재학생", "101호",
            NOW.plusDays(10), NOW.plusDays(10).plusHours(2), NOW.minusDays(1), NOW.plusDays(3),
            recruitType, imageIds, 30, recruitStatus, true, 1L
        );
    }

    private static Event openEvent() {
        return event(RecruitType.OPEN, RecruitStatus.OPEN, List.of());
    }

    @Test
    @DisplayName("행사의 상세 필드를 그대로 옮긴다")
    void copiesDetailFields() {
        EventDetail detail = EventDetail.of(openEvent(), 0, NOW);

        assertEquals(15L, detail.eventId());
        assertEquals("2026 소융대 개강 행사", detail.title());
        assertEquals("소융대 재학생을 위한 개강 행사입니다.", detail.description());
        assertEquals("소융대 재학생", detail.target());
        assertEquals("101호", detail.place());
        assertEquals(NOW.plusDays(10), detail.eventStartAt());
        assertEquals(NOW.plusDays(10).plusHours(2), detail.eventEndAt());
        assertEquals(NOW.minusDays(1), detail.applyStartAt());
        assertEquals(NOW.plusDays(3), detail.applyEndAt());
    }

    /**
     * 모집 상태 판정 규칙 자체는 {@code EventTest}가 덮는다.
     * 여기서는 EventDetail이 저장값을 그대로 베끼지 않고 Event의 계산을 거치는지만 확인한다.
     */
    @Nested
    @DisplayName("모집 상태 위임")
    class RecruitStatusDelegation {

        @Test
        @DisplayName("저장된 모집 상태가 아니라 조회 시점 기준으로 계산한 값을 싣는다")
        void computesInsteadOfCopying() {
            Event event = event(RecruitType.OPEN, RecruitStatus.BEFORE_OPEN, List.of());

            assertEquals(RecruitStatus.BEFORE_OPEN, event.getRecruitStatus());
            assertEquals(RecruitStatus.OPEN, EventDetail.of(event, 0, NOW).recruitStatus());
        }

        @Test
        @DisplayName("신청자 수를 함께 넘겨 정원 마감까지 반영한다")
        void passesApplicantCount() {
            Event event = event(RecruitType.FIRST_COME, RecruitStatus.OPEN, List.of());

            assertEquals(RecruitStatus.OPEN, EventDetail.of(event, 29, NOW).recruitStatus());
            assertEquals(RecruitStatus.CLOSED, EventDetail.of(event, 30, NOW).recruitStatus());
        }
    }

    @Nested
    @DisplayName("마감까지 남은 일수")
    class DaysUntilDeadline {

        @Test
        @DisplayName("모집 중이면 목록과 같은 값을 싣는다")
        void sameAsSummary() {
            Event event = openEvent();

            assertEquals(
                EventSummary.of(event, 0, NOW).daysUntilDeadline(),
                EventDetail.of(event, 0, NOW).daysUntilDeadline());
        }

        @Test
        @DisplayName("모집 중이 아니면 내려보내지 않는다")
        void nullWhenNotOpen() {
            Event forceClosed = event(RecruitType.OPEN, RecruitStatus.CLOSED, List.of());

            assertNull(EventDetail.of(forceClosed, 0, NOW).daysUntilDeadline());
        }
    }

    @Nested
    @DisplayName("이미지 목록")
    class Images {

        @Test
        @DisplayName("행사 이미지 id를 순서대로 싣는다")
        void keepsOrder() {
            Event event = event(RecruitType.OPEN, RecruitStatus.OPEN, List.of(31L, 32L));

            assertEquals(List.of(31L, 32L), EventDetail.of(event, 0, NOW).imageIds());
        }

        @Test
        @DisplayName("이미지가 없으면 null이 아니라 빈 목록이다")
        void emptyListWhenNoImage() {
            assertTrue(EventDetail.of(event(RecruitType.OPEN, RecruitStatus.OPEN, List.of()), 0, NOW)
                .imageIds().isEmpty());
            assertTrue(EventDetail.of(event(RecruitType.OPEN, RecruitStatus.OPEN, null), 0, NOW)
                .imageIds().isEmpty());
        }
    }
}
