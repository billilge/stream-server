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

    // 상태 문자열이 UNPAID/PENDING/PAID가 아닌 경우와, 처리 상태로 PENDING을 지정한 경우를 함께 쓴다
    INVALID_TRANSFER_STATUS(ErrorStatus.BAD_REQUEST, "유효하지 않은 납부 상태입니다."),
    FEE_REQUEST_NOT_FOUND(ErrorStatus.NOT_FOUND, "존재하지 않는 납부 확인 요청입니다."),
    FEE_REQUEST_ALREADY_REVIEWED(ErrorStatus.CONFLICT, "이미 처리된 납부 확인 요청입니다."),
    FEE_TRANSFER_LINK_INVALID(ErrorStatus.INTERNAL_SERVER_ERROR, "송금 요청 URL이 생성되지 않았습니다. 관리자에게 문의해 주세요.");

    private final int status;
    private final String message;
}
