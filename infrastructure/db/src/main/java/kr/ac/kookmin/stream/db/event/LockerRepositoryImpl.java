package kr.ac.kookmin.stream.db.event;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplication;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSection;
import kr.ac.kookmin.stream.event.domain.locker.repository.LockerRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LockerRepositoryImpl implements LockerRepository {

    private static final String LOCKER_UNIQUE_CONSTRAINT = "uk_locker_applications_locker_period_id_locker_id";

    private final LockerPeriodJpaRepository lockerPeriodJpaRepository;
    private final LockerSectionJpaRepository lockerSectionJpaRepository;
    private final LockerJpaRepository lockerJpaRepository;
    private final LockerApplicationJpaRepository lockerApplicationJpaRepository;

    @Override
    public boolean existsPublishedPeriod(Long lockerPeriodId) {
        return lockerPeriodJpaRepository.existsByIdAndIsPublishedTrue(lockerPeriodId);
    }

    @Override
    public boolean existsSection(Long sectionId) {
        return lockerSectionJpaRepository.existsById(sectionId);
    }

    @Override
    public Optional<LockerPeriod> findPublishedPeriodById(Long lockerPeriodId) {
        return lockerPeriodJpaRepository.findByIdAndIsPublishedTrue(lockerPeriodId)
            .map(LockerPeriodJpaEntity::toDomain);
    }

    @Override
    public List<LockerSection> findAllSections() {
        return lockerSectionJpaRepository.findAllByOrderByIdAsc().stream()
            .map(LockerSectionJpaEntity::toDomain)
            .toList();
    }

    @Override
    public List<Locker> findAllLockers() {
        return lockerJpaRepository.findAllByIsDeletedFalse().stream()
            .map(LockerJpaEntity::toDomain)
            .toList();
    }

    @Override
    public List<Locker> findLockersBySectionId(Long sectionId) {
        return lockerJpaRepository.findAllBySectionIdAndIsDeletedFalseOrderByRowNoAscColumnNoAscIdAsc(sectionId).stream()
            .map(LockerJpaEntity::toDomain)
            .toList();
    }

    @Override
    public Optional<Locker> findLockerById(Long lockerId) {
        return lockerJpaRepository.findByIdAndIsDeletedFalse(lockerId)
            .map(LockerJpaEntity::toDomain);
    }

    @Override
    public boolean existsApplication(Long lockerPeriodId, Long lockerId) {
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
    public LockerApplication saveApplication(LockerApplication application) {
        try {
            // 제약 위반을 이 자리에서 잡으려면 커밋 시점까지 미루지 않고 바로 INSERT를 내보내야 한다
            return lockerApplicationJpaRepository.saveAndFlush(LockerApplicationJpaEntity.from(application))
                .toDomain();
        } catch (DataIntegrityViolationException e) {
            if (isViolated(e, LOCKER_UNIQUE_CONSTRAINT)) {
                throw new BusinessException(LockerErrorCode.LOCKER_ALREADY_ASSIGNED);
            }
            throw e;
        }
    }

    /** MySQL은 제약 이름 앞에 테이블명을 붙여 줄 수 있어 포함 여부로 비교한다. */
    private boolean isViolated(DataIntegrityViolationException e, String constraintName) {
        return e.getCause() instanceof ConstraintViolationException violation
            && violation.getConstraintName() != null
            && violation.getConstraintName().contains(constraintName);
    }
}
