package kr.ac.kookmin.stream.db.internal;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;
import kr.ac.kookmin.stream.internal.domain.home.repository.HomeBannerRepository;
import lombok.RequiredArgsConstructor;
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
}
