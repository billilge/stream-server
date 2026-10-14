package kr.ac.kookmin.stream.api.app.welfare.fee.response;

import kr.ac.kookmin.stream.api.app.welfare.fee.FeeAccount;

/** 납부 정보 제출 결과. 학생은 이 계좌와 금액으로 토스에서 송금한다. */
public record FeeSubmitResponse(
    String bank,
    String accountNo,
    Integer remainingSemesters,
    Long transferAmount,
    String transferLinkUrl
) {
    public static FeeSubmitResponse of(int remainingSemesters, FeeAccount account) {
        return new FeeSubmitResponse(
            account.bank(), account.accountNo(), remainingSemesters,
            account.transferAmount(), account.transferLinkUrl());
    }
}
