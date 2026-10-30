package kr.ac.kookmin.stream.event.domain.event.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EventSummaryTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 12, 10, 0);

    private static Event event(
        LocalDateTime applyStartAt,
        LocalDateTime applyEndAt,
        RecruitType recruitType,
        int capacity,
        List<Long> imageIds
    ) {
        return Event.of(
            1L, "행사", "설명", "학생회관",
            NOW.plusDays(30), NOW.plusDays(31), applyStartAt, applyEndAt,
            recruitType, imageIds, capacity, false, true, 100L, null
        );
    }

    private static Event openEvent() {
        return event(NOW.minusDays(1), NOW.plusDays(3), RecruitType.OPEN, 0, List.of());
    }

    /**
     * 모집 상태 판정 규칙 자체는 {@code EventTest}가 덮는다.
     * 여기서는 EventSummary가 Event의 계산을 거치는지만 확인한다.
     */
    @Nested
    @DisplayName("모집 상태 위임")
    class RecruitStatusDelegation {

        @Test
        @DisplayName("신청자 수를 함께 넘겨 정원 마감까지 반영한다")
        void passesApplicantCount() {
            Event event = event(
                NOW.minusDays(1), NOW.plusDays(3), RecruitType.FIRST_COME, 10, List.of());

            assertEquals(RecruitStatus.OPEN, EventSummary.of(event, 9, NOW).recruitStatus());
            assertEquals(RecruitStatus.CLOSED, EventSummary.of(event, 10, NOW).recruitStatus());
        }
    }

    /**
     * 날짜 계산 규칙 자체는 {@code EventTest}가 덮는다. 여기서는 계산 결과가 실리는지만 확인한다.
     */
    @Nested
    @DisplayName("마감까지 남은 일수")
    class DaysUntilDeadline {

        @Test
        @DisplayName("모집 중이면 Event가 계산한 값을 싣고, 그 외에는 비운다")
        void carriesEventCalculation() {
            Event open = event(
                NOW.minusDays(1), NOW.plusDays(3), RecruitType.OPEN, 0, List.of());
            Event closed = event(
                NOW.minusDays(5), NOW.minusDays(1), RecruitType.OPEN, 0, List.of());

            assertEquals(
                open.daysUntilDeadline(NOW, RecruitStatus.OPEN),
                EventSummary.of(open, 0, NOW).daysUntilDeadline());
            assertNull(EventSummary.of(closed, 0, NOW).daysUntilDeadline());
        }
    }

    @Nested
    @DisplayName("대표 이미지")
    class Thumbnail {

        @Test
        @DisplayName("이미지 목록의 첫 번째를 대표로 쓴다")
        void firstImage() {
            Event event = event(
                NOW.minusDays(1), NOW.plusDays(3), RecruitType.OPEN, 0, List.of(7L, 8L, 9L));

            assertEquals(7L, EventSummary.of(event, 0, NOW).thumbnailFileId());
        }

        @Test
        @DisplayName("이미지가 없으면 대표 이미지도 없다")
        void noImage() {
            Event empty = event(
                NOW.minusDays(1), NOW.plusDays(3), RecruitType.OPEN, 0, List.of());
            Event nullImages = event(
                NOW.minusDays(1), NOW.plusDays(3), RecruitType.OPEN, 0, null);

            assertNull(EventSummary.of(empty, 0, NOW).thumbnailFileId());
            assertNull(EventSummary.of(nullImages, 0, NOW).thumbnailFileId());
        }
    }
}
