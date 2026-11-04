package kr.ac.kookmin.stream.db.internal;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplySection;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTarget;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTargetType;
import kr.ac.kookmin.stream.internal.domain.home.repository.HomeApplySectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HomeApplySectionRepositoryImpl implements HomeApplySectionRepository {

    private final HomeApplySectionJpaRepository homeApplySectionJpaRepository;

    @Override
    public List<HomeApplySection> findExposed() {
        return homeApplySectionJpaRepository.findAllByIsExposedTrueOrderByDisplayOrderAscIdAsc().stream()
            .map(HomeApplySectionJpaEntity::toDomain)
            .toList();
    }

    @Override
    public Optional<HomeApplySection> findByTarget(HomeApplyTarget target) {
        return homeApplySectionJpaRepository
            .findByTargetTypeAndTargetId(target.targetType(), target.targetId())
            .map(HomeApplySectionJpaEntity::toDomain);
    }

    @Override
    public List<HomeApplySection> findByTargetTypeAndTargetIds(
        HomeApplyTargetType targetType,
        Collection<Long> targetIds
    ) {
        if (targetIds.isEmpty()) {
            return List.of();
        }
        return homeApplySectionJpaRepository.findAllByTargetTypeAndTargetIdIn(targetType, targetIds).stream()
            .map(HomeApplySectionJpaEntity::toDomain)
            .toList();
    }

    @Override
    public Optional<Integer> findMaxDisplayOrder() {
        return Optional.ofNullable(homeApplySectionJpaRepository.findMaxDisplayOrder());
    }

    @Override
    public HomeApplySection save(HomeApplySection section) {
        return homeApplySectionJpaRepository.save(HomeApplySectionJpaEntity.from(section)).toDomain();
    }

    @Override
    public void saveAll(List<HomeApplySection> sections) {
        homeApplySectionJpaRepository.saveAll(sections.stream().map(HomeApplySectionJpaEntity::from).toList());
    }
}
