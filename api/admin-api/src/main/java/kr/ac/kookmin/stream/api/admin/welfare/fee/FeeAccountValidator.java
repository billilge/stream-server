package kr.ac.kookmin.stream.api.admin.welfare.fee;

import kr.ac.kookmin.stream.api.admin.welfare.fee.request.FeeAccountUpdateRequest;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeBanks;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;

/**
 * 회비 납부 설정 입력 검증. 항목마다 전용 에러 코드를 내려줘야 해서 Bean Validation 대신 직접 본다
 * (fee-domain-study 08번 문서 — 애노테이션으로는 공통 INVALID_INPUT이 된다).
 */
final class FeeAccountValidator {

    private static final int MIN_ACCOUNT_NO_LENGTH = 10;
    private static final int MAX_ACCOUNT_NO_LENGTH = 14;

    private FeeAccountValidator() {}

    static void validate(FeeAccountUpdateRequest request) {
        FeeBanks.validate(request.bank());
        validateAccountNo(request.accountNo());
        validateAmount(request.amount());
    }

    private static void validateAccountNo(String accountNo) {
        if (accountNo == null
            || !accountNo.matches("^[0-9]+$")
            || accountNo.length() < MIN_ACCOUNT_NO_LENGTH
            || accountNo.length() > MAX_ACCOUNT_NO_LENGTH) {
            throw new BusinessException(FeeErrorCode.INVALID_ACCOUNT_NO);
        }
    }

    private static void validateAmount(Integer amount) {
        if (amount == null || amount < 1) {
            throw new BusinessException(FeeErrorCode.INVALID_TRANSFER_AMOUNT);
        }
    }
}
