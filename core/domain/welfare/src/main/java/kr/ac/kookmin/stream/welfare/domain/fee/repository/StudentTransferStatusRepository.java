package kr.ac.kookmin.stream.welfare.domain.fee.repository;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

public interface StudentTransferStatusRepository {
    Optional<StudentTransferStatus> findByMemberId(Long memberId);
    Optional<StudentTransferStatus> findById(Long id);

    // memberIds가 null이면 전체 대상, 빈 리스트면 결과 없음을 의도한 것이므로 호출 전에 걸러낸다(빈 리스트로 호출하지 않는다)
    PageResult<StudentTransferStatus> search(TransferStatus status, List<Long> memberIds, PageOffset pageOffset);

    StudentTransferStatus save(StudentTransferStatus transferStatus);
}
