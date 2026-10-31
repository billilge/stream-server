package kr.ac.kookmin.stream.internal.domain.home.service;

import java.util.Collection;
import java.util.List;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerStatus;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerType;

/**
 * 운영진 홈 화면 관리가 쓰는 조회. 학생 앱이 쓰는 노출 설정 조회는 {@link HomeService}가 맡는다.
 */
public interface AdminHomeService {

    /**
     * 노출이 끝나지 않은 배너를 표시 순서대로 조회한다. 노출 중과 노출 예정이 함께 담긴다.
     * <p>
     * 순서를 드래그로 바꾸는 목록이라 페이지로 자르지 않는다.
     *
     * @param bannerTypes 비어 있으면 분류로 거르지 않는다
     * @param statuses    비어 있으면 상태로 거르지 않는다. 노출 종료는 이 목록에 들어올 수 없다
     */
    List<HomeBanner> getCurrentBanners(Collection<HomeBannerType> bannerTypes, Collection<HomeBannerStatus> statuses);

    /**
     * 노출이 끝난 배너를 종료 일시 최신순으로 페이지 조회한다.
     *
     * @param bannerTypes 비어 있으면 분류로 거르지 않는다
     */
    PageResult<HomeBanner> getPastBanners(Collection<HomeBannerType> bannerTypes, PageOffset pageOffset);

    /**
     * 배너 한 건. 삭제한 배너는 없는 것으로 본다.
     */
    HomeBanner getBanner(Long bannerId);
}
