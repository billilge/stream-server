package kr.ac.kookmin.stream.welfare.domain.fee.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

// 납부 요청은 이력으로 쌓이므로 회원 기준 조회는 모두 최신 요청(PK가 가장 큰 행) 하나만 돌려준다
public interface StudentTransferStatusRepository {
    Optional<StudentTransferStatus> findLatestByMemberId(Long memberId);
    Optional<StudentTransferStatus> findById(Long id);

    // 회원별 최신 요청 중 상태가 statuses에 드는 것만 대상으로 한다. statuses는 비어 있으면 안 된다.
    // memberIds가 null이면 전체 대상, 빈 리스트면 결과 없음을 의도한 것이므로 호출 전에 걸러낸다(빈 리스트로 호출하지 않는다)
    PageResult<StudentTransferStatus> search(Set<TransferStatus> statuses, List<Long> memberIds, PageOffset pageOffset);

    StudentTransferStatus save(StudentTransferStatus transferStatus);
}
