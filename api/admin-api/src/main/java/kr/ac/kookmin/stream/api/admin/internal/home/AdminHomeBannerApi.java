package kr.ac.kookmin.stream.api.admin.internal.home;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.internal.home.request.AdminHomeBannerCategoryParams;
import kr.ac.kookmin.stream.api.admin.internal.home.request.AdminHomeBannerSearchParams;
import kr.ac.kookmin.stream.api.admin.internal.home.response.AdminHomeBannerDetailResponse;
import kr.ac.kookmin.stream.api.admin.internal.home.response.AdminHomeBannerListResponse;
import kr.ac.kookmin.stream.api.admin.internal.home.response.AdminHomeBannerSummaryResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 홈 배너 API의 문서 명세. 구현은 {@link AdminHomeBannerController}가 맡는다.
 * <p>
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다.
 */
@Tag(name = "홈 배너 (운영진)", description = "홈 화면 배너 조회")
public interface AdminHomeBannerApi {

    @Operation(summary = "노출 중 배너 목록 조회",
        description = "노출이 끝나지 않은 배너를 표시 순서대로 전부 조회한다. 노출 중과 노출 예정이 함께 담긴다. "
            + "순서를 드래그로 바꾸는 목록이라 페이지로 자르지 않는다.")
    ApiResponse<AdminHomeBannerListResponse> getCurrentBanners(
        AdminApiUser apiUser,
        @ParameterObject AdminHomeBannerSearchParams params
    );

    @Operation(summary = "지난 배너 목록 조회",
        description = "노출이 끝난 배너를 종료 일시 최신순으로 페이지 조회한다.")
    ApiResponse<PageResponse<AdminHomeBannerSummaryResponse>> getPastBanners(
        AdminApiUser apiUser,
        @ParameterObject AdminHomeBannerCategoryParams params,
        @ParameterObject PageParams pageParams
    );

    @Operation(summary = "배너 상세 조회",
        description = "배너 한 건을 조회한다. 수정 화면의 초기값으로도 쓴다. "
            + "연결된 공지·행사가 지워졌으면 연결 대상만 비워 내려준다. 아직 공개하지 않은 행사는 그대로 내려준다.")
    @ApiErrorCode(type = HomeErrorCode.class, codes = {"HOME_BANNER_NOT_FOUND"})
    ApiResponse<AdminHomeBannerDetailResponse> getBanner(AdminApiUser apiUser, Long bannerId);
}
