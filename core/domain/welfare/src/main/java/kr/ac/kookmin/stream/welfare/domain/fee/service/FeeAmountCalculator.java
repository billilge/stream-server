package kr.ac.kookmin.stream.welfare.domain.fee.service;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;

// 남은 학기 수만큼만 회비를 걷는다 — 남은 학기 수 × 학기당 금액.
// 남은 학기 수는 학생이 직접 입력하고 학점이수현황 캡처로 증빙한다(예전에는 학년·학기로 계산했다).
public final class FeeAmountCalculator {

    private static final int MIN_REMAINING_SEMESTERS = 1;
    private static final int MAX_REMAINING_SEMESTERS = 8;

    private FeeAmountCalculator() {}

    /**
     * 남은 학기 수가 1~8인지 본다. 송금 링크를 받을 때와 납부를 요청할 때 같은 범위를 쓰도록 여기에 둔다.
     */
    public static int validateRemainingSemesters(Integer remainingSemesters) {
        if (remainingSemesters == null
            || remainingSemesters < MIN_REMAINING_SEMESTERS
            || remainingSemesters > MAX_REMAINING_SEMESTERS) {
            throw new BusinessException(FeeErrorCode.INVALID_REMAINING_SEMESTERS);
        }
        return remainingSemesters;
    }

    public static long calculate(int remainingSemesters, long amountPerSemester) {
        return (long) remainingSemesters * amountPerSemester;
    }
}
