package kr.ac.kookmin.stream.common;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 날짜 변환 유틸.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtil {

    /** 서버 기본 타임존에 기대지 않고 명시적으로 한국 표준시를 써야 하는 곳에 쓴다. */
    public static final ZoneId KST = ZoneId.of("Asia/Seoul");

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

    /**
     * DB의 시각(한국 시간 기준 {@code LocalDateTime})을 epoch 밀리초로 바꾼다. 시각을 클라이언트에 내려줄 때 쓰고,
     * 돌려받는 쪽은 {@link #fromEpochMillis(long)}로 되돌린다. 두 방향이 같은 기준(KST)이어야 값이 어긋나지 않는다.
     */
    public static long toEpochMillis(LocalDateTime dateTime) {
        return dateTime.atZone(KST).toInstant().toEpochMilli();
    }

    /**
     * {@link #toEpochMillis(LocalDateTime)}의 반대 방향. epoch 밀리초를 한국 시간 기준 {@code LocalDateTime}으로 바꾼다.
     */
    public static LocalDateTime fromEpochMillis(long epochMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), KST);
    }
}
