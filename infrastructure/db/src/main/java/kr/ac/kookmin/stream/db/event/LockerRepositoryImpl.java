package kr.ac.kookmin.stream.db.event;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSection;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionLayout;
import kr.ac.kookmin.stream.event.domain.locker.repository.LockerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LockerRepositoryImpl implements LockerRepository {

    private final LockerPeriodJpaRepository lockerPeriodJpaRepository;
    private final LockerSectionJpaRepository lockerSectionJpaRepository;
    private final LockerSectionLayoutJpaRepository lockerSectionLayoutJpaRepository;
    private final LockerJpaRepository lockerJpaRepository;

    @Override
    public boolean existsPublishedPeriod(Long lockerPeriodId) {
        return lockerPeriodJpaRepository.existsByIdAndIsPublishedTrue(lockerPeriodId);
    }

    @Override
    public Optional<LockerPeriod> findPublishedPeriodById(Long lockerPeriodId) {
        return lockerPeriodJpaRepository.findByIdAndIsPublishedTrue(lockerPeriodId)
            .map(LockerPeriodJpaEntity::toDomain);
    }

    @Override
    public List<LockerPeriod> findPublishedPeriodsByIds(Collection<Long> lockerPeriodIds) {
        return lockerPeriodJpaRepository.findAllByIdInAndIsPublishedTrue(lockerPeriodIds).stream()
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
    public List<Locker> findLockersByIdsIncludingDeleted(Collection<Long> lockerIds) {
        return lockerJpaRepository.findAllByIdIn(lockerIds).stream()
            .map(LockerJpaEntity::toDomain)
            .toList();
    }
}
