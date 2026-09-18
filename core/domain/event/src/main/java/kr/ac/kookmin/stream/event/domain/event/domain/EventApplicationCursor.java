package kr.ac.kookmin.stream.event.domain.event.domain;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.common.BusinessException;

/**
 * 내 행사 신청 내역의 keyset 커서. 정렬 기준(신청 시각 내림차순 + applicationId 내림차순)과 짝을 이룬다.
 */
public record EventApplicationCursor(LocalDateTime appliedAt, Long applicationId) {

    private static final String JOIN = "|";
    private static final String SPLIT_REGEX = "\\|";
    private static final int PART_COUNT = 2;

    public static EventApplicationCursor of(EventApplicationSummary summary) {
        return new EventApplicationCursor(summary.appliedAt(), summary.applicationId());
    }

    // Base64 인코딩은 웹(Controller) 계층 책임이라 여기서는 순수 문자열 표현만 다룬다
    public static EventApplicationCursor from(String raw) {
        String[] parts = raw.split(SPLIT_REGEX, -1);
        if (parts.length != PART_COUNT) {
            throw new BusinessException(EventErrorCode.EVENT_INVALID_CURSOR);
        }
        try {
            return new EventApplicationCursor(LocalDateTime.parse(parts[0]), Long.valueOf(parts[1]));
        } catch (RuntimeException e) {
            throw new BusinessException(EventErrorCode.EVENT_INVALID_CURSOR);
        }
    }

    public String format() {
        return appliedAt + JOIN + applicationId;
    }
}
