package kr.ac.kookmin.stream.internal.domain.home.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplySection;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;
import kr.ac.kookmin.stream.internal.domain.home.repository.HomeApplySectionRepository;
import kr.ac.kookmin.stream.internal.domain.home.repository.HomeBannerRepository;
import kr.ac.kookmin.stream.internal.domain.home.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class HomeServiceImpl implements HomeService {

    private final HomeBannerRepository homeBannerRepository;
    private final HomeApplySectionRepository homeApplySectionRepository;

    @Override
    public List<HomeBanner> getExposedBanners() {
        // 관리자 쓰기 API가 없는 동안 DB에 손으로 넣은 행이 어긋날 수 있다. 한 건 때문에 홈 전체가
        // 막히지 않도록 그 배너만 빼고 나머지는 보여준다
        return homeBannerRepository.findExposedAt(LocalDateTime.now()).stream()
            .filter(HomeBanner::hasValidLanding)
            .toList();
    }

    @Override
    public List<HomeApplySection> getExposedApplySections() {
        return homeApplySectionRepository.findExposed();
    }
}
