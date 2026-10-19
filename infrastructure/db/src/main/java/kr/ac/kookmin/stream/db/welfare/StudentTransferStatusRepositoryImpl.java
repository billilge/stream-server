package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.db.common.ConstraintViolationUtil;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.repository.StudentTransferStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StudentTransferStatusRepositoryImpl implements StudentTransferStatusRepository {

    private static final String ACTIVE_REQUEST_UNIQUE_CONSTRAINT = "uk_student_transfer_statuses_active_member_id";

    private final StudentTransferStatusJpaRepository studentTransferStatusJpaRepository;

    @Override
    public Optional<StudentTransferStatus> findByMemberId(Long memberId) {
        return studentTransferStatusJpaRepository.findByMemberId(memberId).map(StudentTransferStatusJpaEntity::toDomain);
    }

    @Override
    public List<StudentTransferStatus> findAllByMemberIds(List<Long> memberIds) {
        return studentTransferStatusJpaRepository.findAllByMemberIdIn(memberIds).stream()
            .map(StudentTransferStatusJpaEntity::toDomain)
            .toList();
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
        try {
            // 제약 위반을 이 자리에서 잡으려면 커밋 시점까지 미루지 않고 바로 쓰기를 내보내야 한다
            return studentTransferStatusJpaRepository.saveAndFlush(StudentTransferStatusJpaEntity.from(transferStatus))
                .toDomain();
        } catch (DataIntegrityViolationException e) {
            // 같은 회원에게 진행 중·완료 요청이 이미 있다 — 동시에 들어온 두 번째 요청이 여기서 걸린다
            if (ConstraintViolationUtil.isViolated(e, ACTIVE_REQUEST_UNIQUE_CONSTRAINT)) {
                throw new BusinessException(FeeErrorCode.FEE_REQUEST_ALREADY_REQUESTED);
            }
            throw e;
        }
    }
}
