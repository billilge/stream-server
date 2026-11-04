package kr.ac.kookmin.stream.welfare.domain.fee.service;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeSubmitCommand;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

public interface StudentTransferStatusService {

    /** 회원의 최신 납부 요청 — 곧 회원의 지금 상태다. 한 번도 요청하지 않은 회원(미납부)이면 비어 있다. */
    Optional<StudentTransferStatus> findLatestByMemberId(Long memberId);

    /**
     * 학기 인증과 송금 완료를 한 번에 받아 납부확인중인 새 요청을 만든다. 처음이든 반려 뒤든 늘 새 행이고,
     * 이전 요청은 그대로 남는다. 최신 요청이 납부확인중·납부완료면 {@code FEE_REQUEST_ALREADY_REQUESTED}다.
     *
     * @param amount 요청 시점에 안내한 납부 금액
     */
    StudentTransferStatus submit(Long memberId, FeeSubmitCommand command, long amount);

    // status가 null이면 전체 상태, memberIds가 null이면 회원 필터 없음
    PageResult<StudentTransferStatus> search(TransferStatus status, List<Long> memberIds, PageOffset pageOffset);

    /**
     * 관리자 처리. 이미 처리된 요청도 다시 바꿀 수 있다. rejectReason은 확인필요일 때만 남고,
     * 확인필요인데 비어 있거나 255자를 넘으면 {@code INVALID_REJECT_REASON}다.
     * 회원의 최신 요청만 바꿀 수 있고, 지난 요청이면 {@code FEE_REQUEST_NOT_LATEST}다.
     */
    StudentTransferStatus review(Long transferStatusId, TransferStatus status, String rejectReason);
}
