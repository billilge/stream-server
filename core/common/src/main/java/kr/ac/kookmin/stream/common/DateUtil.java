package kr.ac.kookmin.stream.common;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 날짜 변환 유틸.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtil {

    private static final int FIRST_MONTH = 1;
    private static final int FIRST_DAY = 1;

    /**
     * 연도를 그 해 1월 1일로 바꾼다. 연도 조건을 컬럼에 함수를 씌우지 않는 범위 비교로 만들 때 쓴다.
     *
     * @param year 대상 연도. null이면 null을 돌려 "기간 조건 없음"을 그대로 전달한다
     */
    public static LocalDate startOfYear(Integer year) {
        return year == null ? null : LocalDate.of(year, FIRST_MONTH, FIRST_DAY);
    }

    /**
     * 연도를 다음 해 1월 1일로 바꾼다. {@link #startOfYear(Integer)}와 짝지어 반개구간의 끝 경계로 쓴다.
     */
    public static LocalDate startOfNextYear(Integer year) {
        return year == null ? null : LocalDate.of(year + 1, FIRST_MONTH, FIRST_DAY);
    }
}
