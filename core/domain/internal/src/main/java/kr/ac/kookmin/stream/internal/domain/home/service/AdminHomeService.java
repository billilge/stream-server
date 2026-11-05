package kr.ac.kookmin.stream.internal.domain.home.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplySection;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTarget;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerCategory;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerCreateCommand;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerStatus;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerUpdateCommand;

/**
 * 운영진 홈 화면 관리. 학생 앱이 쓰는 노출 설정 조회는 {@link HomeService}가 맡는다.
 */
public interface AdminHomeService {

    /**
     * 노출이 끝나지 않은 배너를 표시 순서대로 조회한다. 노출 중과 노출 예정이 함께 담긴다.
     * <p>
     * 순서를 드래그로 바꾸는 목록이라 페이지로 자르지 않는다.
     *
     * @param now        노출 상태를 판정할 기준 시각. 거르는 시각과 응답에 실리는 시각이 같아야 상태가 엇갈리지 않아
     *                   부르는 쪽이 한 번 잡아 넘긴다
     * @param categories 비어 있으면 카테고리로 거르지 않는다
     * @param statuses   비어 있으면 상태로 거르지 않는다. 노출 종료는 이 목록에 들어올 수 없다
     */
    List<HomeBanner> getCurrentBanners(
        LocalDateTime now, Collection<HomeBannerCategory> categories, Collection<HomeBannerStatus> statuses);

    /**
     * 노출이 끝난 배너를 종료 일시 최신순으로 페이지 조회한다.
     *
     * @param now        노출이 끝났는지 판정할 기준 시각
     * @param categories 비어 있으면 카테고리로 거르지 않는다
     */
    PageResult<HomeBanner> getPastBanners(
        LocalDateTime now, Collection<HomeBannerCategory> categories, PageOffset pageOffset);

    /**
     * 배너 한 건. 삭제한 배너는 없는 것으로 본다.
     */
    HomeBanner getBanner(Long bannerId);

    /**
     * 배너를 등록한다. 표시 순서는 맨 뒤로 정한다.
     */
    void createBanner(HomeBannerCreateCommand command);

    /**
     * 배너의 입력값을 통째로 바꾼다. 표시 순서는 그대로 둔다.
     */
    void updateBanner(Long bannerId, HomeBannerUpdateCommand command);

    /**
     * 배너를 소프트 삭제한다.
     */
    void deleteBanner(Long bannerId);

    /**
     * 노출이 끝나지 않은 배너의 표시 순서를 받은 순서대로 다시 매긴다.
     * <p>
     * 지금 끝나지 않은 배너가 받은 목록에 모두 들어 있어야 한다. 하나라도 빠졌으면 운영진이 보고 있던
     * 화면과 서버가 다른 상태라는 뜻이라 덮어쓰지 않고 멈춘다.
     * <p>
     * 반대로 서버 목록에 없는 식별자는 건너뛴다. 끝나지 않은 배너는 등록·수정 때만 늘고 그 밖에는 줄기만
     * 하므로, 목록을 띄워둔 사이 노출이 끝났거나 지워진 배너가 섞여 있을 뿐이다.
     *
     * @param orderedBannerIds 바뀐 순서대로의 배너 식별자 전체
     */
    void updateBannerOrders(List<Long> orderedBannerIds);

    /**
     * 후보의 설정을 표시 순서대로 조회한다. 설정이 아직 없는 후보는 미노출로 채워 설정된 것들 뒤에
     * 후보 순서대로 붙인다 — 조회는 저장하지 않는다. 후보가 아닌 설정 행은 돌려주지 않는다.
     *
     * @param exposed null이면 노출 여부로 거르지 않는다
     */
    List<HomeApplySection> getApplySections(List<HomeApplyTarget> candidates, Boolean exposed);

    /**
     * 대상의 노출 여부를 바꾼다. 설정이 아직 없던 대상이면 이때 만든다.
     */
    void updateApplySectionExposure(HomeApplyTarget target, boolean exposed);

    /**
     * 받은 대상에 0부터 표시 순서를 다시 매긴다. 설정이 아직 없던 대상은 이때 미노출로 만든다.
     * <p>
     * 배너와 달리 후보 전체가 들어 있는지 보지 않는다. 후보는 신청 기간 경과와 선착순 정원 충족으로도
     * 바뀌므로 "목록이 달라졌다"가 다른 운영진이 건드렸다는 신호가 되지 못한다. 목록에 없던 후보는
     * 설정이 없는 채로 남아 뒤에 붙고, 그사이 마감된 대상은 순서만 받고 후보에서 빠진다.
     */
    void updateApplySectionOrders(List<HomeApplyTarget> orderedTargets);
}
