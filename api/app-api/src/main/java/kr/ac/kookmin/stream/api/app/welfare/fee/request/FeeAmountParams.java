package kr.ac.kookmin.stream.api.app.welfare.fee.request;

/**
 * @param remainingSemesters 남은 학기 수. 주면 그 값으로 납부 금액과 송금 링크를 계산하고, 없으면 계산하지 않는다.
 *                           범위(1~8)는 회비 정책이라 {@code RemainingSemesters}가 검증한다
 */
public record FeeAmountParams(
    Integer remainingSemesters
) {
}
