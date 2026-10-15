package kr.ac.kookmin.stream.api.admin.welfare.fee;

import kr.ac.kookmin.stream.welfare.domain.fee.service.FeeAmountCalculator;
import kr.ac.kookmin.stream.welfare.domain.fee.service.TossTransferLinkGenerator;

/**
 * 관리자가 설정한 회비 납부 설정. 계좌는 전역 하나이고 학년도별로 나누지 않는다.
 * <p>
 * {@code api:app-api}에도 같은 모양의 record가 있다. 공유하지 않는 이유는 둘을 담을 자리가 없어서다 —
 * {@code api:common-api}는 welfare·internal 모듈을 모르고, 그 의존을 추가하면 "role 무관 공통 인프라"라는
 * 그 모듈의 역할이 깨진다. 설정 키는 {@code FeeConfigKeys} 하나를 같이 쓰므로 값이 어긋나지는 않는다.
 *
 * @param bank      설정 전이면 null
 * @param accountNo 설정 전이면 null
 * @param amount    학기당 회비. 설정 전이면 기본값
 */
public record FeeAccount(String bank, String accountNo, long amount) {

    public boolean isConfigured() {
        return bank != null && accountNo != null;
    }

    /** 금액이 빠진 송금 기본 링크. 계좌가 설정되기 전이면 null이다. */
    public String transferLinkUrl() {
        return isConfigured() ? TossTransferLinkGenerator.generate(bank, accountNo, null) : null;
    }

    /** 남은 학기 수를 낸 학생의 납부 금액. 아직 내지 않았으면 null이다. */
    public Long transferAmountFor(Integer remainingSemesters) {
        return remainingSemesters == null ? null : FeeAmountCalculator.calculate(remainingSemesters, amount);
    }
}
