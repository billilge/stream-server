package kr.ac.kookmin.stream.welfare.domain.fee.domain;

import kr.ac.kookmin.stream.common.BusinessException;

/**
 * 학생이 입력한 남은 학기 수. 회비는 남은 학기 수만큼만 걷고, 학점이수현황 캡처로 증빙한다.
 */
public record RemainingSemesters(int value) {

    private static final int MIN = 1;
    private static final int MAX = 8;

    public static RemainingSemesters of(int value) {
        validateRange(value);
        return new RemainingSemesters(value);
    }

    /** 이 학생이 낼 금액. 남은 학기 수 × 학기당 회비. */
    public long calculateAmount(long amountPerSemester) {
        return (long) value * amountPerSemester;
    }

    private static void validateRange(int value) {
        if (value < MIN || value > MAX) {
            throw new BusinessException(FeeErrorCode.INVALID_REMAINING_SEMESTERS);
        }
    }
}
