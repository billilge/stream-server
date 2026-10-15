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
    INVALID_BANK(ErrorStatus.BAD_REQUEST, "지원하지 않는 은행입니다."),
    INVALID_ACCOUNT_NO(ErrorStatus.BAD_REQUEST, "유효하지 않은 계좌번호입니다."),
    INVALID_TRANSFER_AMOUNT(ErrorStatus.BAD_REQUEST, "유효하지 않은 학생회비 금액입니다."),
    INVALID_ADMIN_NOTE(ErrorStatus.BAD_REQUEST, "유효하지 않은 관리자 안내 문구입니다."),
    FEE_ACCOUNT_NOT_FOUND(ErrorStatus.NOT_FOUND, "학생회비 납부 계좌가 등록되지 않았습니다."),
    FEE_REQUEST_NOT_FOUND(ErrorStatus.NOT_FOUND, "제출된 납부 정보가 없습니다."),
    FEE_REQUEST_ALREADY_REQUESTED(ErrorStatus.CONFLICT, "이미 납부 확인을 요청했거나 납부가 완료되었습니다."),
    MEMBER_IS_NOT_PAYER(ErrorStatus.BAD_REQUEST, "회비를 납부한 회원만 이용할 수 있습니다.");

    private final int status;
    private final String message;
}
