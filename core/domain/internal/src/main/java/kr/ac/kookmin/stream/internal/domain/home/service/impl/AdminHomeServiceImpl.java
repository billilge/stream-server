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
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerCategory;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerCreateCommand;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerStatus;
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
        Collection<HomeBannerCategory> categories,
        Collection<HomeBannerStatus> statuses
    ) {
        // 노출 중과 노출 예정을 가르는 일은 HomeBanner가 한다. SQL에 같은 판정을 두면 둘이 어긋난다.
        // 끝나지 않은 배너는 많아야 몇 건이라 전부 읽어 걸러도 된다
        LocalDateTime now = LocalDateTime.now();
        return homeBannerRepository.findNotEndedAt(now, categories).stream()
            .filter(banner -> statuses.isEmpty() || statuses.contains(banner.calculateStatus(now)))
            .toList();
    }

    @Override
    public PageResult<HomeBanner> getPastBanners(Collection<HomeBannerCategory> categories, PageOffset pageOffset) {
        return homeBannerRepository.findEndedBefore(LocalDateTime.now(), categories, pageOffset);
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
        Map<Long, HomeBanner> bannersById = homeBannerRepository.findNotEndedAt(LocalDateTime.now(), List.of())
            .stream()
            .collect(Collectors.toMap(HomeBanner::getId, Function.identity()));

        // 목록을 띄워둔 사이 노출이 끝났거나 지워진 배너는 서버 목록에 없다. 건너뛰어 자연 만료만으로
        // 드래그가 막히지 않게 한다 — 끝나지 않은 배너는 등록·수정 때만 늘고 그 밖에는 줄기만 한다
        List<Long> targetIds = orderedBannerIds.stream()
            .filter(bannersById::containsKey)
            .toList();
        validateCoversAll(bannersById.keySet(), targetIds);

        List<HomeBanner> reordered = new ArrayList<>(targetIds.size());
        for (int displayOrder = 0; displayOrder < targetIds.size(); displayOrder++) {
            HomeBanner banner = bannersById.get(targetIds.get(displayOrder));
            banner.changeDisplayOrder(displayOrder);
            reordered.add(banner);
        }
        homeBannerRepository.saveAll(reordered);
    }

    /**
     * 지금 끝나지 않은 배너가 요청에 모두 들어 있는지. 하나라도 빠졌으면 필터를 켠 일부 목록이거나 그사이
     * 배너가 늘어난 것이라, 보낸 순서로 전체를 다시 매길 수 없다.
     */
    private void validateCoversAll(Set<Long> currentIds, List<Long> targetIds) {
        // 서버 목록에 있는 식별자만 남은 뒤라, 같은 식별자가 두 번 왔으면 개수가 어긋난다
        if (targetIds.size() != currentIds.size() || !Set.copyOf(targetIds).equals(currentIds)) {
            throw new BusinessException(HomeErrorCode.HOME_BANNER_ORDER_MISMATCH);
        }
    }
}
