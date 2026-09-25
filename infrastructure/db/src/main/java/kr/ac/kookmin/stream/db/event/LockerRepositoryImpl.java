package kr.ac.kookmin.stream.db.event;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSection;
import kr.ac.kookmin.stream.event.domain.locker.repository.LockerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LockerRepositoryImpl implements LockerRepository {

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
    public Set<Long> findAppliedLockerIds(Long lockerPeriodId) {
        return lockerApplicationJpaRepository.findLockerIdsByLockerPeriodId(lockerPeriodId);
    }

    @Override
    public Optional<Long> findAppliedLockerId(Long lockerPeriodId, Long memberId) {
        return lockerApplicationJpaRepository.findLockerIdByLockerPeriodIdAndMemberId(lockerPeriodId, memberId);
    }
}
