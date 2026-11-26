package kr.ac.kookmin.stream.internal.domain.display.domain;

import java.time.LocalDate;
import kr.ac.kookmin.stream.common.BusinessException;

/**
 * 시작일~종료일(양끝 포함)로 이루어진 날짜 기간. 학사 일정의 기간과 학사 일정 조회 범위에 함께 쓴다.
 * <p>
 * 종료일이 시작일보다 앞서면 안 된다는 순서는 이 값 자체의 불변식이라 {@link #of}에서 검증한다. 시작일과 종료일이 같은
 * 하루 기간은 허용한다. DB에서 복원할 때는 저장 전에 검증을 거친 값이라 표준 생성자로 만든다.
 */
public record DisplaySchedulePeriod(LocalDate startDate, LocalDate endDate) {

    public static DisplaySchedulePeriod of(LocalDate startDate, LocalDate endDate) {
        validateOrder(startDate, endDate);
        return new DisplaySchedulePeriod(startDate, endDate);
    }

    // 시작일·종료일 당일도 기간에 든다
    public boolean includes(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    private static void validateOrder(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new BusinessException(DisplayErrorCode.INVALID_DISPLAY_SCHEDULE_PERIOD);
        }
    }
}
