package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferRequest;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.repository.FeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FeeRepositoryImpl implements FeeRepository {

    private final StudentTransferRequestJpaRepository studentTransferRequestJpaRepository;

    @Override
    public Optional<StudentTransferRequest> findByMemberId(Long memberId) {
        return studentTransferRequestJpaRepository.findByMemberId(memberId).map(StudentTransferRequestJpaEntity::toDomain);
    }

    @Override
    public Optional<StudentTransferRequest> findById(Long id) {
        return studentTransferRequestJpaRepository.findById(id).map(StudentTransferRequestJpaEntity::toDomain);
    }

    @Override
    public PageResult<StudentTransferRequest> search(TransferStatus status, List<Long> memberIds, int page, int size) {
        boolean filterByMember = memberIds != null;
        List<Long> ids = filterByMember ? memberIds : List.of();

        Page<StudentTransferRequestJpaEntity> result = studentTransferRequestJpaRepository.search(
            status, filterByMember, ids, PageRequest.of(page, size)
        );

        return new PageResult<>(
            result.getContent().stream().map(StudentTransferRequestJpaEntity::toDomain).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages()
        );
    }

    @Override
    public StudentTransferRequest save(StudentTransferRequest request) {
        return studentTransferRequestJpaRepository.save(StudentTransferRequestJpaEntity.from(request)).toDomain();
    }
}
