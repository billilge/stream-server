package kr.ac.kookmin.stream.db.internal;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerType;
import kr.ac.kookmin.stream.internal.domain.home.repository.HomeBannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HomeBannerRepositoryImpl implements HomeBannerRepository {

    private final HomeBannerJpaRepository homeBannerJpaRepository;

    @Override
    public List<HomeBanner> findExposedAt(LocalDateTime at) {
        return homeBannerJpaRepository.findExposedAt(at).stream()
            .map(HomeBannerJpaEntity::toDomain)
            .toList();
    }

    @Override
    public List<HomeBanner> findNotEndedAt(LocalDateTime at, Collection<HomeBannerType> bannerTypes) {
        return homeBannerJpaRepository.findNotEndedAt(at, !bannerTypes.isEmpty(), bannerTypes).stream()
            .map(HomeBannerJpaEntity::toDomain)
            .toList();
    }

    @Override
    public PageResult<HomeBanner> findEndedBefore(
        LocalDateTime at,
        Collection<HomeBannerType> bannerTypes,
        PageOffset pageOffset
    ) {
        Page<HomeBannerJpaEntity> result = homeBannerJpaRepository.findEndedBefore(
            at,
            !bannerTypes.isEmpty(),
            bannerTypes,
            PageRequest.of(pageOffset.page(), pageOffset.size())
        );

        return PageResult.of(
            result.getContent().stream().map(HomeBannerJpaEntity::toDomain).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements()
        );
    }

    @Override
    public Optional<HomeBanner> findById(Long bannerId) {
        return homeBannerJpaRepository.findByIdAndIsDeletedFalse(bannerId).map(HomeBannerJpaEntity::toDomain);
    }
}
