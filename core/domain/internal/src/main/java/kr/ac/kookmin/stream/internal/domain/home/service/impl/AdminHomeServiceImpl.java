package kr.ac.kookmin.stream.internal.domain.home.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerCreateCommand;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerStatus;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerType;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerUpdateCommand;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeErrorCode;
import kr.ac.kookmin.stream.internal.domain.home.repository.HomeBannerRepository;
import kr.ac.kookmin.stream.internal.domain.home.service.AdminHomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 최댓값 조회와 저장을 한 트랜잭션으로 묶지만, 두 운영진이 동시에 등록하면 같은 순서값이 붙을 수 있다.
    // 목록이 id로 2차 정렬하므로 순서가 깨지지는 않고 둘의 앞뒤만 등록 순으로 갈린다
    @Override
    @Transactional
    public void createBanner(HomeBannerCreateCommand command) {
        int displayOrder = homeBannerRepository.findMaxDisplayOrder()
            .map(maxOrder -> maxOrder + 1)
            .orElse(0);
        homeBannerRepository.save(HomeBanner.create(command, displayOrder));
    }

    @Override
    @Transactional
    public void updateBanner(Long bannerId, HomeBannerUpdateCommand command) {
        HomeBanner banner = getBanner(bannerId);
        banner.update(command);
        homeBannerRepository.save(banner);
    }

    @Override
    @Transactional
    public void deleteBanner(Long bannerId) {
        if (!homeBannerRepository.delete(bannerId)) {
            throw new BusinessException(HomeErrorCode.HOME_BANNER_NOT_FOUND);
        }
    }

    // 노출이 끝난 배너의 순서값은 손대지 않는다. 지난 배너 목록은 노출 종료 일시로 정렬하고 노출 중 목록은
    // 끝나지 않은 배너만 읽으므로, 두 무리의 순서값이 겹쳐도 어느 목록도 흐트러지지 않는다
    @Override
    @Transactional
    public void updateBannerOrders(List<Long> orderedBannerIds) {
        List<HomeBanner> current = homeBannerRepository.findNotEndedAt(LocalDateTime.now(), List.of());
        validateCoversAll(current, orderedBannerIds);

        Map<Long, HomeBanner> bannersById = current.stream()
            .collect(Collectors.toMap(HomeBanner::getId, Function.identity()));

        List<HomeBanner> reordered = new ArrayList<>(orderedBannerIds.size());
        for (int displayOrder = 0; displayOrder < orderedBannerIds.size(); displayOrder++) {
            HomeBanner banner = bannersById.get(orderedBannerIds.get(displayOrder));
            banner.changeDisplayOrder(displayOrder);
            reordered.add(banner);
        }
        homeBannerRepository.saveAll(reordered);
    }

    private void validateCoversAll(List<HomeBanner> current, List<Long> orderedBannerIds) {
        Set<Long> currentIds = current.stream()
            .map(HomeBanner::getId)
            .collect(Collectors.toUnmodifiableSet());
        Set<Long> requestedIds = Set.copyOf(orderedBannerIds);

        // 같은 식별자가 두 번 와도 집합끼리는 같아 보이므로 개수를 함께 본다
        if (orderedBannerIds.size() != requestedIds.size() || !requestedIds.equals(currentIds)) {
            throw new BusinessException(HomeErrorCode.HOME_BANNER_ORDER_MISMATCH);
        }
    }
}
