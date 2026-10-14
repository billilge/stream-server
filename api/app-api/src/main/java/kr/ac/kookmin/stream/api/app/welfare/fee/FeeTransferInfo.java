package kr.ac.kookmin.stream.api.app.welfare.fee;

/**
 * 회비 송금에 필요한 정보. 계좌는 전역 하나이고 금액만 학생마다 다르다.
 *
 * @param transferAmount  남은 학기 수 × 학기당 금액. 금액을 정할 수 없으면 null
 * @param transferLinkUrl 금액이 붙은 토스 송금 링크. 금액을 모르면 null
 */
public record FeeTransferInfo(String bank, String accountNo, Long transferAmount, String transferLinkUrl) {

    public static FeeTransferInfo accountOnly(String bank, String accountNo) {
        return new FeeTransferInfo(bank, accountNo, null, null);
    }
}
