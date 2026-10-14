package kr.ac.kookmin.stream.api.app.welfare.fee;

/**
 * 회비 납부 계좌와, 그 계좌로 보낼 금액·송금 링크. 계좌는 전역 하나이고 금액만 학생마다 다르다.
 *
 * @param transferAmount  남은 학기 수 × 학기당 금액. 남은 학기 수를 모르면 null
 * @param transferLinkUrl 금액이 붙은 토스 송금 링크. 금액을 모르면 null
 */
public record FeeAccount(String bank, String accountNo, Long transferAmount, String transferLinkUrl) {
}
