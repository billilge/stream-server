package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.PaymentStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentFee;
import kr.ac.kookmin.stream.welfare.domain.fee.repository.FeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FeeRepositoryImpl implements FeeRepository {

    private final StudentFeeJpaRepository studentFeeJpaRepository;

    @Override
    public Optional<StudentFee> findByMemberId(Long memberId) {
        return studentFeeJpaRepository.findByMemberId(memberId).map(StudentFeeJpaEntity::toDomain);
    }

    @Override
    public Optional<StudentFee> findById(Long id) {
        return studentFeeJpaRepository.findById(id).map(StudentFeeJpaEntity::toDomain);
    }

    @Override
    public PageResult<StudentFee> search(PaymentStatus status, List<Long> memberIds, int page, int size) {
        boolean filterByMember = memberIds != null;
        List<Long> ids = filterByMember ? memberIds : List.of();

        Page<StudentFeeJpaEntity> result = studentFeeJpaRepository.search(
            status, filterByMember, ids, PageRequest.of(page, size)
        );

        return new PageResult<>(
            result.getContent().stream().map(StudentFeeJpaEntity::toDomain).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages()
        );
    }

    @Override
    public StudentFee save(StudentFee fee) {
        return studentFeeJpaRepository.save(StudentFeeJpaEntity.from(fee)).toDomain();
    }
}
