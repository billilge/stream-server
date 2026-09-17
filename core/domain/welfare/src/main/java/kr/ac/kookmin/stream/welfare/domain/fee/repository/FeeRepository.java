package kr.ac.kookmin.stream.welfare.domain.fee.repository;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentFee;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

public interface FeeRepository {
    Optional<StudentFee> findByMemberId(Long memberId);
    Optional<StudentFee> findById(Long id);

    // memberIds가 null이면 전체 대상, 빈 리스트면 결과 없음을 의도한 것이므로 호출 전에 걸러낸다(빈 리스트로 호출하지 않는다)
    PageResult<StudentFee> search(TransferStatus status, List<Long> memberIds, int page, int size);

    StudentFee save(StudentFee fee);
}
