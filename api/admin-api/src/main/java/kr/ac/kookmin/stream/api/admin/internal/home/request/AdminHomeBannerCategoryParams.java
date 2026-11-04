package kr.ac.kookmin.stream.api.admin.internal.home.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerCategory;

/**
 * 지난 배너 목록 필터. 노출 상태는 전부 노출 종료라 받지 않는다.
 *
 * @param categories 비우면 카테고리로 거르지 않는다
 */
public record AdminHomeBannerCategoryParams(

    @Schema(description = "카테고리 필터. 생략하면 전체")
    List<HomeBannerCategory> categories
) {

    public List<HomeBannerCategory> categoriesOrEmpty() {
        return categories == null ? List.of() : categories;
    }
}
