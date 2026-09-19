package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.repository.StudentTransferStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StudentTransferStatusRepositoryImpl implements StudentTransferStatusRepository {

    private final StudentTransferStatusJpaRepository studentTransferStatusJpaRepository;

    @Override
    public Optional<StudentTransferStatus> findByMemberId(Long memberId) {
        return studentTransferStatusJpaRepository.findByMemberId(memberId).map(StudentTransferStatusJpaEntity::toDomain);
    }

    @Override
    public Optional<StudentTransferStatus> findById(Long id) {
        return studentTransferStatusJpaRepository.findById(id).map(StudentTransferStatusJpaEntity::toDomain);
    }

    @Override
    public PageResult<StudentTransferStatus> search(TransferStatus status, List<Long> memberIds, PageOffset pageOffset) {
        boolean filterByMember = memberIds != null;
        List<Long> ids = filterByMember ? memberIds : List.of();

        Page<StudentTransferStatusJpaEntity> result = studentTransferStatusJpaRepository.search(
            status, filterByMember, ids, PageRequest.of(pageOffset.page(), pageOffset.size())
        );

        return PageResult.of(
            result.getContent().stream().map(StudentTransferStatusJpaEntity::toDomain).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements()
        );
    }

    @Override
    public StudentTransferStatus save(StudentTransferStatus transferStatus) {
        return studentTransferStatusJpaRepository.save(StudentTransferStatusJpaEntity.from(transferStatus)).toDomain();
    }
}
