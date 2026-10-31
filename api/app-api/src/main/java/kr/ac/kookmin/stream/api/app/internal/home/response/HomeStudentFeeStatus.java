package kr.ac.kookmin.stream.api.app.internal.home.response;

import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

/**
 * 홈에 보여줄 학생회비 납부 상태.
 * <p>
 * 한 번도 납부를 신청하지 않은 회원은 {@code student_transfer_statuses}에 행이 없어 저장된 상태가 없다.
 * 화면은 그 경우도 하나의 상태로 그리므로 {@link #NOT_PAID}를 여기서 만든다.
 */
public enum HomeStudentFeeStatus {

    NOT_PAID,
    PENDING,
    PAID,
    REJECTED;

    public static HomeStudentFeeStatus from(TransferStatus status) {
        return status == null ? NOT_PAID : valueOf(status.name());
    }
}
