package kr.ac.kookmin.stream.event.domain.event.domain;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.Cursor;

/**
 * 행사 목록의 keyset 커서. 목록은 모집 상태 구간마다 정렬 기준이 달라서, 마지막 행사가 속한 구간과
 * 그 구간의 정렬 키(sortAt) + eventId를 함께 담는다.
 * <p>
 * 구간별 정렬 기준 — 모집 중은 신청 마감 일시 오름차순, 모집 예정은 신청 시작 일시 오름차순,
 * 모집 종료는 행사 시작 일시 내림차순. 동일 시각은 eventId로 정렬 방향을 따라 끊는다.
 */
public record EventCursor(RecruitStatus recruitStatus, LocalDateTime sortAt, Long eventId) implements Cursor {

    private static final String JOIN = "|";
    private static final int PART_COUNT = 3;

    public static EventCursor of(EventSummary summary) {
        return new EventCursor(summary.recruitStatus(), sortAtOf(summary), summary.eventId());
    }

    public static EventCursor from(String raw) {
        try {
            List<String> parts = Cursor.parseParts(raw, PART_COUNT, EventErrorCode.EVENT_INVALID_CURSOR);
            return new EventCursor(
                RecruitStatus.valueOf(parts.get(0)), LocalDateTime.parse(parts.get(1)), Long.valueOf(parts.get(2)));
        } catch (RuntimeException e) {
            throw new BusinessException(EventErrorCode.EVENT_INVALID_CURSOR);
        }
    }

    @Override
    public String format() {
        return recruitStatus + JOIN + sortAt + JOIN + eventId;
    }

    private static LocalDateTime sortAtOf(EventSummary summary) {
        return switch (summary.recruitStatus()) {
            case OPEN -> summary.applyEndAt();
            case BEFORE_OPEN -> summary.applyStartAt();
            case CLOSED -> summary.eventStartAt();
        };
    }
}
