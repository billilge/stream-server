package kr.ac.kookmin.stream.db.internal;

import java.util.List;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplySection;
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
}
