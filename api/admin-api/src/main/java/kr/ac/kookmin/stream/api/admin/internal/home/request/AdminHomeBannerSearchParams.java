package kr.ac.kookmin.stream.api.admin.internal.home.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerStatus;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerType;

/**
 * 배너 목록 필터. {@code @ModelAttribute}로 받으므로 잘못된 enum 문자열은 400으로 떨어진다 —
 * {@code @RequestParam}으로 받으면 변환 실패가 500이 된다.
 *
 * @param bannerTypes 비우면 분류로 거르지 않는다
 * @param statuses    비우면 상태로 거르지 않는다. 노출 중 탭 전용이라 노출 종료는 들어올 수 없다
 */
public record AdminHomeBannerSearchParams(

    @Schema(description = "분류 필터. 생략하면 전체")
    List<HomeBannerType> bannerTypes,

    @Schema(description = "노출 상태 필터. 생략하면 전체")
    List<HomeBannerStatus> statuses
) {

    public List<HomeBannerType> bannerTypesOrEmpty() {
        return bannerTypes == null ? List.of() : bannerTypes;
    }

    public List<HomeBannerStatus> statusesOrEmpty() {
        return statuses == null ? List.of() : statuses;
    }
}
