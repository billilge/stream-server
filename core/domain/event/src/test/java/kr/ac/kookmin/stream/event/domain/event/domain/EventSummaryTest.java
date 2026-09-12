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
        RecruitStatus recruitStatus,
        List<Long> imageIds
    ) {
        return Event.of(
            1L, "행사", "설명", "전교생", "학생회관",
            NOW.plusDays(30), NOW.plusDays(31), applyStartAt, applyEndAt,
            recruitType, imageIds, capacity, recruitStatus, true, 100L
        );
    }

    private static Event openEvent() {
        return event(NOW.minusDays(1), NOW.plusDays(3), RecruitType.OPEN, 0, RecruitStatus.OPEN, List.of());
    }

    /**
     * 모집 상태 판정 규칙 자체는 {@code EventTest}가 덮는다.
     * 여기서는 EventSummary가 저장값을 그대로 베끼지 않고 Event의 계산을 거치는지만 확인한다.
     */
    @Nested
    @DisplayName("모집 상태 위임")
    class RecruitStatusDelegation {

        @Test
        @DisplayName("저장된 모집 상태가 아니라 조회 시점 기준으로 계산한 값을 싣는다")
        void computesInsteadOfCopying() {
            Event event = event(
                NOW.minusDays(1), NOW.plusDays(3), RecruitType.OPEN, 0, RecruitStatus.BEFORE_OPEN, List.of());

            assertEquals(RecruitStatus.BEFORE_OPEN, event.getRecruitStatus());
            assertEquals(RecruitStatus.OPEN, EventSummary.of(event, 0, NOW).recruitStatus());
        }

        @Test
        @DisplayName("신청자 수를 함께 넘겨 정원 마감까지 반영한다")
        void passesApplicantCount() {
            Event event = event(
                NOW.minusDays(1), NOW.plusDays(3), RecruitType.FIRST_COME, 10, RecruitStatus.OPEN, List.of());

            assertEquals(RecruitStatus.OPEN, EventSummary.of(event, 9, NOW).recruitStatus());
            assertEquals(RecruitStatus.CLOSED, EventSummary.of(event, 10, NOW).recruitStatus());
        }
    }

    @Nested
    @DisplayName("마감까지 남은 일수")
    class DaysUntilDeadline {

        @Test
        @DisplayName("모집 중이면 신청 마감일까지 남은 날짜 수를 센다")
        void countsDays() {
            Event event = event(
                NOW.minusDays(1), NOW.plusDays(3), RecruitType.OPEN, 0, RecruitStatus.OPEN, List.of());

            assertEquals(3, EventSummary.of(event, 0, NOW).daysUntilDeadline());
        }

        @Test
        @DisplayName("마감 당일이면 0이다")
        void deadlineToday() {
            Event event = event(
                NOW.minusDays(1), NOW.withHour(23).withMinute(59), RecruitType.OPEN, 0, RecruitStatus.OPEN, List.of());

            assertEquals(0, EventSummary.of(event, 0, NOW).daysUntilDeadline());
        }

        @Test
        @DisplayName("시각이 아니라 날짜로 세므로 23시간 뒤 마감도 하루 뒤면 1이다")
        void countsByDateNotByHours() {
            Event event = event(
                NOW.minusDays(1), NOW.plusDays(1).withHour(9), RecruitType.OPEN, 0, RecruitStatus.OPEN, List.of());

            assertEquals(1, EventSummary.of(event, 0, NOW).daysUntilDeadline());
        }

        @Test
        @DisplayName("모집 중이 아니면 내려보내지 않는다")
        void nullWhenNotOpen() {
            Event beforeOpen = event(
                NOW.plusDays(1), NOW.plusDays(5), RecruitType.OPEN, 0, RecruitStatus.OPEN, List.of());
            Event closed = event(
                NOW.minusDays(5), NOW.minusDays(1), RecruitType.OPEN, 0, RecruitStatus.OPEN, List.of());

            assertNull(EventSummary.of(beforeOpen, 0, NOW).daysUntilDeadline());
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
                NOW.minusDays(1), NOW.plusDays(3), RecruitType.OPEN, 0, RecruitStatus.OPEN, List.of(7L, 8L, 9L));

            assertEquals(7L, EventSummary.of(event, 0, NOW).thumbnailFileId());
        }

        @Test
        @DisplayName("이미지가 없으면 대표 이미지도 없다")
        void noImage() {
            Event empty = event(
                NOW.minusDays(1), NOW.plusDays(3), RecruitType.OPEN, 0, RecruitStatus.OPEN, List.of());
            Event nullImages = event(
                NOW.minusDays(1), NOW.plusDays(3), RecruitType.OPEN, 0, RecruitStatus.OPEN, null);

            assertNull(EventSummary.of(empty, 0, NOW).thumbnailFileId());
            assertNull(EventSummary.of(nullImages, 0, NOW).thumbnailFileId());
        }
    }
}
