package kr.ac.kookmin.stream.db.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.db.common.ConstraintViolationUtil;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSection;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionLayout;
import kr.ac.kookmin.stream.event.domain.locker.repository.LockerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LockerRepositoryImpl implements LockerRepository {

    private static final String SEMESTER_UNIQUE_CONSTRAINT = "uk_locker_periods_active_semester";

    private final LockerPeriodJpaRepository lockerPeriodJpaRepository;
    private final LockerSectionJpaRepository lockerSectionJpaRepository;
    private final LockerSectionLayoutJpaRepository lockerSectionLayoutJpaRepository;
    private final LockerJpaRepository lockerJpaRepository;

    @Override
    public List<LockerPeriod> findAllPeriods() {
        return lockerPeriodJpaRepository.findAllByIsDeletedFalseOrderByApplyStartAtDesc().stream()
            .map(LockerPeriodJpaEntity::toDomain)
            .toList();
    }

    @Override
    public Optional<LockerPeriod> findPeriodById(Long lockerPeriodId) {
        return lockerPeriodJpaRepository.findByIdAndIsDeletedFalse(lockerPeriodId)
            .map(LockerPeriodJpaEntity::toDomain);
    }

    @Override
    public LockerPeriod savePeriod(LockerPeriod period) {
        try {
            // 같은 학기를 동시에 등록하면 유니크 제약에 걸린다. 제약 위반을 이 자리에서 잡으려면 커밋 시점까지 미루지 않고
            // 바로 INSERT를 내보내야 한다
            return lockerPeriodJpaRepository.saveAndFlush(LockerPeriodJpaEntity.from(period)).toDomain();
        } catch (DataIntegrityViolationException e) {
            if (ConstraintViolationUtil.isViolated(e, SEMESTER_UNIQUE_CONSTRAINT)) {
                throw new BusinessException(LockerErrorCode.LOCKER_PERIOD_SEMESTER_DUPLICATED);
            }
            throw e;
        }
    }

    // 조회한 엔티티를 바로 바꿔 더티 체킹에 맡긴다. 존재 확인과 삭제가 한 쿼리로 묶이고, save(merge) 경로를 타지 않아
    // 도메인 객체가 삭제 상태를 들고 있을 필요가 없다
    @Override
    public boolean deletePeriod(Long lockerPeriodId) {
        return lockerPeriodJpaRepository.findByIdAndIsDeletedFalse(lockerPeriodId)
            .map(entity -> {
                entity.delete();
                return true;
            })
            .orElse(false);
    }

    @Override
    public boolean existsPublishedPeriod(Long lockerPeriodId) {
        return lockerPeriodJpaRepository.existsByIdAndIsPublishedTrueAndIsDeletedFalse(lockerPeriodId);
    }

    @Override
    public Optional<LockerPeriod> findPublishedPeriodById(Long lockerPeriodId) {
        return lockerPeriodJpaRepository.findByIdAndIsPublishedTrueAndIsDeletedFalse(lockerPeriodId)
            .map(LockerPeriodJpaEntity::toDomain);
    }

    @Override
    public List<LockerPeriod> findPublishedPeriodsByIds(Collection<Long> lockerPeriodIds) {
        return lockerPeriodJpaRepository.findAllByIdInAndIsPublishedTrueAndIsDeletedFalse(lockerPeriodIds).stream()
            .map(LockerPeriodJpaEntity::toDomain)
            .toList();
    }

    @Override
    public List<LockerPeriod> findPublishedPeriodsApplyEndingBetween(LocalDateTime from, LocalDateTime to) {
        return lockerPeriodJpaRepository.findPublishedApplyEndingBetween(from, to).stream()
            .map(LockerPeriodJpaEntity::toDomain)
            .toList();
    }

    @Override
    public List<LockerPeriod> findPublishedPeriodsUsageEndingOn(LocalDate usageEndAt) {
        return lockerPeriodJpaRepository.findAllByIsPublishedTrueAndIsDeletedFalseAndUsageEndAt(usageEndAt).stream()
            .map(LockerPeriodJpaEntity::toDomain)
            .toList();
    }

    @Override
    public List<LockerSection> findAllSections() {
        return lockerSectionJpaRepository.findAllByOrderByIdAsc().stream()
            .map(LockerSectionJpaEntity::toDomain)
            .toList();
    }

    @Override
    public Optional<LockerSection> findSectionById(Long sectionId) {
        return lockerSectionJpaRepository.findById(sectionId)
            .map(LockerSectionJpaEntity::toDomain);
    }

    @Override
    public Optional<LockerSectionLayout> findLayoutBySectionId(Long sectionId) {
        return lockerSectionLayoutJpaRepository.findBySectionId(sectionId)
            .map(LockerSectionLayoutJpaEntity::toDomain);
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
    public void saveLockers(List<Locker> lockers) {
        lockerJpaRepository.saveAll(lockers.stream()
            .map(LockerJpaEntity::from)
            .toList());
    }

    @Override
    public List<Locker> findLockersByIds(Collection<Long> lockerIds) {
        if (lockerIds.isEmpty()) {
            return List.of();
        }
        return lockerJpaRepository.findAllByIdInAndIsDeletedFalse(lockerIds).stream()
            .map(LockerJpaEntity::toDomain)
            .toList();
    }

    @Override
    public List<Locker> findLockersByIdsIncludingDeleted(Collection<Long> lockerIds) {
        return lockerJpaRepository.findAllByIdIn(lockerIds).stream()
            .map(LockerJpaEntity::toDomain)
            .toList();
    }
}
