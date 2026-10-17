package kr.ac.kookmin.stream.welfare.domain.fee.service;

import java.util.List;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

public interface StudentTransferStatusService {

    StudentTransferStatus getByMemberId(Long memberId);

    /**
     * 납부 정보(남은 학기 수·학점이수현황 캡처)를 저장한다. 상태는 납부 전 그대로다.
     * 행이 없으면 만들고, 있으면 이전 입력값을 덮어쓴다.
     */
    StudentTransferStatus submit(Long memberId, int remainingSemesters, String proofImageKey);

    /** 송금 확인을 요청한다. 납부 정보를 제출하지 않았으면 {@code FEE_REQUEST_NOT_FOUND}다. */
    StudentTransferStatus requestConfirmation(Long memberId);

    // status가 null이면 전체 상태, memberIds가 null이면 회원 필터 없음
    PageResult<StudentTransferStatus> search(TransferStatus status, List<Long> memberIds, PageOffset pageOffset);

    StudentTransferStatus review(Long transferStatusId, TransferStatus status);
}
