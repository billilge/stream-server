package kr.ac.kookmin.stream.event.domain.event.domain;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.Cursor;

/**
 * 내 행사 신청 내역의 keyset 커서. 정렬 기준(신청 시각 내림차순 + applicationId 내림차순)과 짝을 이룬다.
 */
public record EventApplicationCursor(LocalDateTime appliedAt, Long applicationId) implements Cursor {

    private static final String JOIN = "|";
    private static final int PART_COUNT = 2;

    public static EventApplicationCursor of(EventApplicationSummary summary) {
        return new EventApplicationCursor(summary.appliedAt(), summary.applicationId());
    }

    // Base64 인코딩은 웹(Controller) 계층 책임이라 여기서는 순수 문자열 표현만 다룬다
    public static EventApplicationCursor from(String raw) {
        try {
            List<String> parts =
                Cursor.parseParts(raw, PART_COUNT, EventErrorCode.EVENT_INVALID_CURSOR);
            return new EventApplicationCursor(
                LocalDateTime.parse(parts.get(0)), Long.valueOf(parts.get(1)));
        } catch (RuntimeException e) {
            throw new BusinessException(EventErrorCode.EVENT_INVALID_CURSOR);
        }
    }

    @Override
    public String format() {
        return appliedAt + JOIN + applicationId;
    }
}
