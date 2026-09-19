package kr.ac.kookmin.stream.welfare.domain.fee.service;

import java.util.List;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

public interface StudentTransferStatusService {

    StudentTransferStatus getByMemberId(Long memberId);

    StudentTransferStatus requestConfirmation(Long memberId);

    // status가 null이면 전체 상태, memberIds가 null이면 회원 필터 없음
    PageResult<StudentTransferStatus> search(TransferStatus status, List<Long> memberIds, PageOffset pageOffset);

    StudentTransferStatus review(Long transferStatusId, TransferStatus status);
}
