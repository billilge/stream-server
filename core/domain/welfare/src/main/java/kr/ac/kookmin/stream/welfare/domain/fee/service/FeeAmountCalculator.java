package kr.ac.kookmin.stream.welfare.domain.fee.service;

import kr.ac.kookmin.stream.welfare.domain.fee.domain.GradeSemester;

// 4년(8학기)치 회비를 남은 학기 수만큼만 걷는다 — (전체 8학기 - 이수완료학기) * 학기당 금액.
// 이수완료학기는 학년·학기로 계산한다: 1학년 1학기면 0학기 이수, 2학년 1학기면 2학기 이수하는 식.
public final class FeeAmountCalculator {

    private static final int TOTAL_SEMESTERS = 8;

    private FeeAmountCalculator() {}

    public static long calculate(GradeSemester gradeSemester, long amountPerSemester) {
        int completedSemesters = (gradeSemester.grade() - 1) * 2 + (gradeSemester.semester() - 1);
        int remainingSemesters = Math.max(TOTAL_SEMESTERS - completedSemesters, 0);
        return (long) remainingSemesters * amountPerSemester;
    }
}
