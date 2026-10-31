package kr.ac.kookmin.stream.api.admin.internal.home.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerType;

/**
 * 지난 배너 목록 필터. 노출 상태는 전부 노출 종료라 받지 않는다.
 *
 * @param bannerTypes 비우면 분류로 거르지 않는다
 */
public record AdminHomeBannerTypeParams(

    @Schema(description = "분류 필터. 생략하면 전체")
    List<HomeBannerType> bannerTypes
) {

    public List<HomeBannerType> bannerTypesOrEmpty() {
        return bannerTypes == null ? List.of() : bannerTypes;
    }
}
