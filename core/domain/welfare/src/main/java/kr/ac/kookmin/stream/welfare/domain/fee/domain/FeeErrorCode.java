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

    // 상태 문자열이 UNPAID/PENDING/PAID/NEEDS_CHECK가 아닌 경우에 쓴다
    INVALID_TRANSFER_STATUS(ErrorStatus.BAD_REQUEST, "유효하지 않은 납부 상태입니다."),
    INVALID_REMAINING_SEMESTERS(ErrorStatus.BAD_REQUEST, "유효하지 않은 남은 학기 수입니다."),
    INVALID_PROOF_IMAGE(ErrorStatus.BAD_REQUEST, "유효하지 않은 인증 이미지입니다."),
    FEE_ACCOUNT_NOT_FOUND(ErrorStatus.NOT_FOUND, "학생회비 납부 계좌가 등록되지 않았습니다."),
    FEE_REQUEST_NOT_FOUND(ErrorStatus.NOT_FOUND, "제출된 납부 정보가 없습니다."),
    FEE_REQUEST_ALREADY_REQUESTED(ErrorStatus.CONFLICT, "이미 납부 확인을 요청했거나 납부가 완료되었습니다."),
    FEE_REQUEST_ALREADY_REVIEWED(ErrorStatus.CONFLICT, "이미 처리된 납부 확인 요청입니다."),
    FEE_TRANSFER_LINK_INVALID(ErrorStatus.INTERNAL_SERVER_ERROR, "송금 요청 URL이 생성되지 않았습니다. 관리자에게 문의해 주세요."),
    MEMBER_IS_NOT_PAYER(ErrorStatus.BAD_REQUEST, "회비를 납부한 회원만 이용할 수 있습니다.");

    private final int status;
    private final String message;
}
