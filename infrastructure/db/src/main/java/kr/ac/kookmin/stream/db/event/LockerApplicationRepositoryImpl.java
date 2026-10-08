package kr.ac.kookmin.stream.db.event;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.db.common.ConstraintViolationUtil;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplication;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode;
import kr.ac.kookmin.stream.event.domain.locker.repository.LockerApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LockerApplicationRepositoryImpl implements LockerApplicationRepository {

    private static final String LOCKER_UNIQUE_CONSTRAINT = "uk_locker_applications_locker_period_id_locker_id";

    private final LockerApplicationJpaRepository lockerApplicationJpaRepository;

    @Override
    public boolean existsByLocker(Long lockerPeriodId, Long lockerId) {
        return lockerApplicationJpaRepository.existsByLockerPeriodIdAndLockerId(lockerPeriodId, lockerId);
    }

    @Override
    public Set<Long> findAppliedLockerIds(Long lockerPeriodId) {
        return lockerApplicationJpaRepository.findLockerIdsByLockerPeriodId(lockerPeriodId);
    }

    @Override
    public Optional<Long> findAppliedLockerId(Long lockerPeriodId, Long memberId) {
        return lockerApplicationJpaRepository.findLockerIdByLockerPeriodIdAndMemberId(lockerPeriodId, memberId);
    }

    @Override
    public List<LockerApplication> findByMemberId(Long memberId) {
        return lockerApplicationJpaRepository.findAllByMemberIdOrderByAppliedAtDescIdDesc(memberId).stream()
            .map(LockerApplicationJpaEntity::toDomain)
            .toList();
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
            throw e;
        }
    }
}
