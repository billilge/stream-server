package kr.ac.kookmin.stream.db.event;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.db.common.ConstraintViolationUtil;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplication;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode;
import kr.ac.kookmin.stream.event.domain.locker.repository.LockerApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LockerApplicationRepositoryImpl implements LockerApplicationRepository {

    // V26에서 유니크 둘을 generated column(active_locker_key·active_member_key)으로 옮겼다. 취소된 신청이 자리를
    // 잡지 않도록 한 것이라 제약 이름도 함께 바뀐다
    private static final String LOCKER_UNIQUE_CONSTRAINT = "uk_locker_applications_active_locker_key";
    private static final String MEMBER_UNIQUE_CONSTRAINT = "uk_locker_applications_active_member_key";

    private final LockerApplicationJpaRepository lockerApplicationJpaRepository;

    @Override
    public boolean existsByPeriod(Long lockerPeriodId) {
        return lockerApplicationJpaRepository.existsByLockerPeriodId(lockerPeriodId);
    }

    @Override
    public boolean existsByLocker(Long lockerPeriodId, Long lockerId) {
        return lockerApplicationJpaRepository.existsByLockerPeriodIdAndLockerIdAndIsCanceledFalse(lockerPeriodId, lockerId);
    }

    @Override
    public Set<Long> findAppliedLockerIds(Long lockerPeriodId) {
        return lockerApplicationJpaRepository.findLockerIdsByLockerPeriodId(lockerPeriodId);
    }

    @Override
    public List<Long> findMemberIds(Long lockerPeriodId) {
        return lockerApplicationJpaRepository.findMemberIdsByLockerPeriodId(lockerPeriodId);
    }

    @Override
    public Optional<Long> findAppliedLockerId(Long lockerPeriodId, Long memberId) {
        return lockerApplicationJpaRepository.findLockerIdByLockerPeriodIdAndMemberId(lockerPeriodId, memberId);
    }

    @Override
    public boolean existsByPeriodIdsAndLockerIds(Collection<Long> lockerPeriodIds, Collection<Long> lockerIds) {
        if (lockerPeriodIds.isEmpty() || lockerIds.isEmpty()) {
            return false;
        }
        return lockerApplicationJpaRepository.existsByLockerPeriodIdInAndLockerIdInAndIsCanceledFalse(lockerPeriodIds, lockerIds);
    }

    @Override
    public List<LockerApplication> findByMemberId(Long memberId) {
        return lockerApplicationJpaRepository.findAllByMemberIdAndIsCanceledFalseOrderByAppliedAtDescIdDesc(memberId).stream()
            .map(LockerApplicationJpaEntity::toDomain)
            .toList();
    }

    @Override
    public Optional<LockerApplication> findById(Long lockerApplicationId) {
        return lockerApplicationJpaRepository.findById(lockerApplicationId)
            .map(LockerApplicationJpaEntity::toDomain);
    }

    @Override
    public PageResult<LockerApplication> search(Long lockerPeriodId, List<Long> memberIds, PageOffset pageOffset) {
        boolean filterByMember = memberIds != null;
        List<Long> ids = filterByMember ? memberIds : List.of();

        Page<LockerApplicationJpaEntity> result = lockerApplicationJpaRepository.search(
            lockerPeriodId, filterByMember, ids, PageRequest.of(pageOffset.page(), pageOffset.size())
        );

        return PageResult.of(
            result.getContent().stream().map(LockerApplicationJpaEntity::toDomain).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements()
        );
    }

    @Override
    public LockerApplication save(LockerApplication application) {
        try {
            // 제약 위반을 이 자리에서 잡으려면 커밋 시점까지 미루지 않고 바로 INSERT를 내보내야 한다
            return lockerApplicationJpaRepository.saveAndFlush(LockerApplicationJpaEntity.from(application))
                .toDomain();
        } catch (DataIntegrityViolationException e) {
            if (ConstraintViolationUtil.isViolated(e, LOCKER_UNIQUE_CONSTRAINT)) {
                throw new BusinessException(LockerErrorCode.LOCKER_ALREADY_ASSIGNED);
            }
            // 회원 쪽 제약도 여기서 잡는다. 안 잡으면 운영진이 이미 칸을 가진 학생에게 배정할 때 500이 나간다
            if (ConstraintViolationUtil.isViolated(e, MEMBER_UNIQUE_CONSTRAINT)) {
                throw new BusinessException(LockerErrorCode.MEMBER_ALREADY_HAS_LOCKER);
            }
            throw e;
        }
    }
}
