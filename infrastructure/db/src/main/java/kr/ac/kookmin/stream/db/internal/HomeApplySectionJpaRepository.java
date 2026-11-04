package kr.ac.kookmin.stream.db.internal;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HomeApplySectionJpaRepository extends JpaRepository<HomeApplySectionJpaEntity, Long> {

    List<HomeApplySectionJpaEntity> findAllByIsExposedTrueOrderByDisplayOrderAscIdAsc();

    Optional<HomeApplySectionJpaEntity> findByTargetTypeAndTargetId(HomeApplyTargetType targetType, Long targetId);

    List<HomeApplySectionJpaEntity> findAllByTargetTypeAndTargetIdIn(
        HomeApplyTargetType targetType, Collection<Long> targetIds);

    @Query("SELECT MAX(s.displayOrder) FROM HomeApplySectionJpaEntity s")
    Integer findMaxDisplayOrder();
}
