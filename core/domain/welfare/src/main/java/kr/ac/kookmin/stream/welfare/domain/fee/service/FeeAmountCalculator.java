package kr.ac.kookmin.stream.welfare.domain.fee.service;

// 남은 학기 수만큼만 회비를 걷는다 — 남은 학기 수 × 학기당 금액.
// 남은 학기 수는 학생이 직접 입력하고 학점이수현황 캡처로 증빙한다(예전에는 학년·학기로 계산했다).
public final class FeeAmountCalculator {

    private FeeAmountCalculator() {}

    public static long calculate(int remainingSemesters, long amountPerSemester) {
        return (long) remainingSemesters * amountPerSemester;
    }
}
