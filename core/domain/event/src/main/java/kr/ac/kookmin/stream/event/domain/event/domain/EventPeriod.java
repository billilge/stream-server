package kr.ac.kookmin.stream.event.domain.event.domain;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.common.BusinessException;

/**
 * 시작·종료 일시로 이루어진 기간. 행사 기간과 신청 기간에 함께 쓴다.
 * <p>
 * 종료가 시작보다 늦어야 한다는 순서는 이 값 자체의 불변식이라 {@link #of}에서 검증한다. DB에서 복원할 때는 표준
 * 생성자로 만들어 검증하지 않는다. {@code event_end_at}이 NULL인 기존 행이 있어 검증하면 조회가 깨진다.
 */
public record EventPeriod(LocalDateTime startAt, LocalDateTime endAt) {

    public static EventPeriod of(LocalDateTime startAt, LocalDateTime endAt) {
        validateOrder(startAt, endAt);
        return new EventPeriod(startAt, endAt);
    }

    private static void validateOrder(LocalDateTime startAt, LocalDateTime endAt) {
        if (!endAt.isAfter(startAt)) {
            throw new BusinessException(EventErrorCode.EVENT_INVALID_PERIOD);
        }
    }
}
