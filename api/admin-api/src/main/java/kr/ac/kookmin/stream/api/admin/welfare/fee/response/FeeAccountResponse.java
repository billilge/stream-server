package kr.ac.kookmin.stream.api.admin.welfare.fee.response;

import kr.ac.kookmin.stream.api.admin.welfare.fee.FeeAccount;

/** 회비 납부 설정. 조회와 변경이 같은 모양을 쓴다. */
public record FeeAccountResponse(
    String bank,
    String accountNo,
    long amount,
    String transferLinkUrl
) {
    public static FeeAccountResponse from(FeeAccount account) {
        return new FeeAccountResponse(
            account.bank(), account.accountNo(), account.amount(), account.generateTransferLinkUrl());
    }
}
