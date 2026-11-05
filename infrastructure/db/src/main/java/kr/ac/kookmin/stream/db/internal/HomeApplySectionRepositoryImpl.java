package kr.ac.kookmin.stream.db.internal;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.db.common.ConstraintViolationUtil;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplySection;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTarget;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTargetType;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeErrorCode;
import kr.ac.kookmin.stream.internal.domain.home.repository.HomeApplySectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HomeApplySectionRepositoryImpl implements HomeApplySectionRepository {

    /**
     * 같은 대상의 설정 행이 둘이 되지 않게 막는 유니크 키(V34). 설정이 아직 없던 대상을 두 운영진이 동시에
     * 켜거나 순서에 넣으면 둘 다 INSERT를 내보내 늦은 쪽이 여기에 걸린다.
     */
    private static final String TARGET_UNIQUE_CONSTRAINT = "uk_home_apply_sections_target_type_target_id";

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
        try {
            // 제약 위반을 이 자리에서 잡으려면 커밋 시점까지 미루지 않고 바로 쓰기를 내보내야 한다
            return homeApplySectionJpaRepository.saveAndFlush(HomeApplySectionJpaEntity.from(section)).toDomain();
        } catch (DataIntegrityViolationException e) {
            throw translateViolation(e);
        }
    }

    @Override
    public void saveAll(List<HomeApplySection> sections) {
        try {
            // 같은 이유로 바로 내보낸다. 한 건이라도 걸리면 트랜잭션이 통째로 돌아가 순서는 손대기 전 그대로 남는다
            homeApplySectionJpaRepository.saveAllAndFlush(
                sections.stream().map(HomeApplySectionJpaEntity::from).toList());
        } catch (DataIntegrityViolationException e) {
            throw translateViolation(e);
        }
    }

    private RuntimeException translateViolation(DataIntegrityViolationException e) {
        if (ConstraintViolationUtil.isViolated(e, TARGET_UNIQUE_CONSTRAINT)) {
            return new BusinessException(HomeErrorCode.HOME_APPLY_SECTION_CONFLICT);
        }
        return e;
    }
}
