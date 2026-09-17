package kr.ac.kookmin.stream.welfare.domain.fee.domain;

import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.common.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum FeeErrorCode implements ErrorCode {

    INVALID_TRANSFER_STATUS(ErrorStatus.BAD_REQUEST, "유효하지 않은 납부 상태입니다."),
    INVALID_FEE_STATUS(ErrorStatus.BAD_REQUEST, "납부 상태는 PAID 또는 UNPAID만 설정할 수 있습니다."),
    FEE_REQUEST_NOT_FOUND(ErrorStatus.NOT_FOUND, "존재하지 않는 납부 확인 요청입니다."),
    FEE_REQUEST_ALREADY_REVIEWED(ErrorStatus.CONFLICT, "이미 처리된 납부 확인 요청입니다."),
    INVALID_FEE_AMOUNT(ErrorStatus.BAD_REQUEST, "유효하지 않은 금액입니다.");

    private final int status;
    private final String message;
}
