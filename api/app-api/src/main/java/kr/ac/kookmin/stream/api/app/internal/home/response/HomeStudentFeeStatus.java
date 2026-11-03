package kr.ac.kookmin.stream.api.app.internal.home.response;

import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

/**
 * 홈에 보여줄 학생회비 납부 상태.
 * <p>
 * 한 번도 납부를 신청하지 않은 회원은 {@code student_transfer_statuses}에 행이 없어 저장된 상태가 없다.
 * 화면은 그 경우도 하나의 상태로 그리므로 {@link #UNPAID}를 여기서 만든다 — 같은 뜻을 내려주는
 * {@code GET /v1/app/fee}가 이미 {@code UNPAID}를 쓰므로 프론트가 한 이름으로 분기하도록 맞췄다.
 */
public enum HomeStudentFeeStatus {

    UNPAID,
    PENDING,
    PAID,
    REJECTED;

    public static HomeStudentFeeStatus from(TransferStatus status) {
        if (status == null) {
            return UNPAID;
        }
        // switch 식이라 TransferStatus에 값이 늘면 컴파일이 막는다. 이름으로 찾으면 그때 홈 전체가 500이 된다
        return switch (status) {
            case PENDING -> PENDING;
            case PAID -> PAID;
            case REJECTED -> REJECTED;
        };
    }
}
