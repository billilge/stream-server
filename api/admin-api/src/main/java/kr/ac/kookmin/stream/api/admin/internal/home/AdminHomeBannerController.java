package kr.ac.kookmin.stream.api.admin.internal.home;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.internal.home.request.AdminHomeBannerCategoryParams;
import kr.ac.kookmin.stream.api.admin.internal.home.request.AdminHomeBannerSearchParams;
import kr.ac.kookmin.stream.api.admin.internal.home.response.AdminHomeBannerDetailResponse;
import kr.ac.kookmin.stream.api.admin.internal.home.response.AdminHomeBannerListItemResponse;
import kr.ac.kookmin.stream.api.admin.internal.home.response.AdminHomeBannerListResponse;
import kr.ac.kookmin.stream.api.admin.internal.home.usecase.AdminHomeBannerDetailUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;
import kr.ac.kookmin.stream.internal.domain.home.service.AdminHomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/home/banners")
@RequiredArgsConstructor
public class AdminHomeBannerController implements AdminHomeBannerApi {

    private final AdminHomeService adminHomeService;
    private final AdminHomeBannerDetailUseCase adminHomeBannerDetailUseCase;

    @Override
    @GetMapping
    public ApiResponse<AdminHomeBannerListResponse> getCurrentBanners(
        AdminApiUser apiUser,
        @Valid @ModelAttribute AdminHomeBannerSearchParams params
    ) {
        // 거르는 시각과 응답에 실리는 시각이 달라지면 statuses로 거른 결과에 다른 상태가 섞인다
        LocalDateTime now = LocalDateTime.now();
        List<HomeBanner> banners =
            adminHomeService.getCurrentBanners(now, params.categoriesOrEmpty(), params.statusesOrEmpty());
        return ApiResponse.success(AdminHomeBannerListResponse.of(banners, now));
    }

    @Override
    @GetMapping("/past")
    public ApiResponse<PageResponse<AdminHomeBannerListItemResponse>> getPastBanners(
        AdminApiUser apiUser,
        @Valid @ModelAttribute AdminHomeBannerCategoryParams params,
        @Valid @ModelAttribute PageParams pageParams
    ) {
        LocalDateTime now = LocalDateTime.now();
        PageResult<HomeBanner> result =
            adminHomeService.getPastBanners(now, params.categoriesOrEmpty(), pageParams.toOffset());
        return ApiResponse.success(
            PageResponse.from(result, banner -> AdminHomeBannerListItemResponse.of(banner, now)));
    }

    @Override
    @GetMapping("/{bannerId}")
    public ApiResponse<AdminHomeBannerDetailResponse> getBanner(
        AdminApiUser apiUser,
        @PathVariable Long bannerId
    ) {
        return ApiResponse.success(adminHomeBannerDetailUseCase.getBanner(bannerId));
    }
}
