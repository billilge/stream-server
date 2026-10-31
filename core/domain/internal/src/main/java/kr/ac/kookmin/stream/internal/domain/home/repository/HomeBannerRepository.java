package kr.ac.kookmin.stream.internal.domain.home.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerType;

public interface HomeBannerRepository {

    /**
     * {@code at}이 노출 기간 안인 배너를 표시 순서대로 조회한다. 삭제한 배너는 뺀다.
     */
    List<HomeBanner> findExposedAt(LocalDateTime at);

    /**
     * 노출이 아직 끝나지 않은 배너를 표시 순서대로 조회한다. 노출 중과 노출 예정이 함께 담긴다 —
     * 둘을 가르는 일은 {@link HomeBanner#calculateStatus}가 하므로 여기서는 끝났는지만 본다.
     * <p>
     * 운영진 목록은 순서를 드래그로 바꾸므로 페이지로 자르지 않는다. 노출 중인 배너는 많아야 몇 건이다.
     *
     * @param bannerTypes 비어 있으면 분류로 거르지 않는다
     */
    List<HomeBanner> findNotEndedAt(LocalDateTime at, Collection<HomeBannerType> bannerTypes);

    /**
     * 노출이 끝난 배너를 노출 종료 일시 최신순으로 페이지 조회한다. 학기가 지나며 쌓이므로 페이지로 자른다.
     *
     * @param bannerTypes 비어 있으면 분류로 거르지 않는다
     */
    PageResult<HomeBanner> findEndedBefore(
        LocalDateTime at, Collection<HomeBannerType> bannerTypes, PageOffset pageOffset);

    /**
     * 배너 한 건. 삭제한 배너는 없는 것으로 본다.
     */
    Optional<HomeBanner> findById(Long bannerId);
}
