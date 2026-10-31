package kr.ac.kookmin.stream.internal.domain.home.service.impl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerStatus;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerType;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeErrorCode;
import kr.ac.kookmin.stream.internal.domain.home.repository.HomeBannerRepository;
import kr.ac.kookmin.stream.internal.domain.home.service.AdminHomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class AdminHomeServiceImpl implements AdminHomeService {

    private final HomeBannerRepository homeBannerRepository;

    @Override
    public List<HomeBanner> getCurrentBanners(
        Collection<HomeBannerType> bannerTypes,
        Collection<HomeBannerStatus> statuses
    ) {
        // 노출 중과 노출 예정을 가르는 일은 HomeBanner가 한다. SQL에 같은 판정을 두면 둘이 어긋난다.
        // 끝나지 않은 배너는 많아야 몇 건이라 전부 읽어 걸러도 된다
        LocalDateTime now = LocalDateTime.now();
        return homeBannerRepository.findNotEndedAt(now, bannerTypes).stream()
            .filter(banner -> statuses.isEmpty() || statuses.contains(banner.calculateStatus(now)))
            .toList();
    }

    @Override
    public PageResult<HomeBanner> getPastBanners(Collection<HomeBannerType> bannerTypes, PageOffset pageOffset) {
        return homeBannerRepository.findEndedBefore(LocalDateTime.now(), bannerTypes, pageOffset);
    }

    @Override
    public HomeBanner getBanner(Long bannerId) {
        return homeBannerRepository.findById(bannerId)
            .orElseThrow(() -> new BusinessException(HomeErrorCode.HOME_BANNER_NOT_FOUND));
    }
}
