package kr.ac.kookmin.stream.api.app.welfare.fee;

import kr.ac.kookmin.stream.welfare.domain.fee.service.FeeAmountCalculator;
import kr.ac.kookmin.stream.welfare.domain.fee.service.TossTransferLinkGenerator;

/**
 * 관리자가 설정한 회비 납부 설정. 셋 다 {@code admin_config_values}에 들어 있고 같이 읽힌다.
 * 계좌는 전역 하나이며, 학생마다 다른 것은 금액뿐이다.
 *
 * @param amountPerSemester 학기당 회비
 */
public record FeeAccount(String bank, String accountNo, long amountPerSemester) {

    /** 이 학생이 보낼 금액. 남은 학기 수 × 학기당 회비. */
    public long calculateTransferAmount(int remainingSemesters) {
        return FeeAmountCalculator.calculate(remainingSemesters, amountPerSemester);
    }

    /** 금액이 붙은 토스 송금 링크. */
    public String generateTransferLinkUrl(long transferAmount) {
        return TossTransferLinkGenerator.generate(bank, accountNo, transferAmount);
    }
}
