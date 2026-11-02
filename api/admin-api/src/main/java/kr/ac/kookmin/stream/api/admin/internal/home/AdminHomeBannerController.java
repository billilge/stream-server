package kr.ac.kookmin.stream.api.admin.internal.home;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.internal.home.request.AdminHomeBannerCategoryParams;
import kr.ac.kookmin.stream.api.admin.internal.home.request.AdminHomeBannerOrderUpdateRequest;
import kr.ac.kookmin.stream.api.admin.internal.home.request.AdminHomeBannerSearchParams;
import kr.ac.kookmin.stream.api.admin.internal.home.request.AdminHomeBannerWriteRequest;
import kr.ac.kookmin.stream.api.admin.internal.home.response.AdminHomeBannerDetailResponse;
import kr.ac.kookmin.stream.api.admin.internal.home.response.AdminHomeBannerListResponse;
import kr.ac.kookmin.stream.api.admin.internal.home.response.AdminHomeBannerSummaryResponse;
import kr.ac.kookmin.stream.api.admin.internal.home.usecase.AdminHomeBannerDetailUseCase;
import kr.ac.kookmin.stream.api.admin.internal.home.usecase.AdminHomeBannerWriteUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;
import kr.ac.kookmin.stream.internal.domain.home.service.AdminHomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/home/banners")
@RequiredArgsConstructor
public class AdminHomeBannerController implements AdminHomeBannerApi {

    private final AdminHomeService adminHomeService;
    private final AdminHomeBannerDetailUseCase adminHomeBannerDetailUseCase;
    private final AdminHomeBannerWriteUseCase adminHomeBannerWriteUseCase;

    @Override
    @GetMapping
    public ApiResponse<AdminHomeBannerListResponse> getCurrentBanners(
        AdminApiUser apiUser,
        @Valid @ModelAttribute AdminHomeBannerSearchParams params
    ) {
        List<HomeBanner> banners =
            adminHomeService.getCurrentBanners(params.categoriesOrEmpty(), params.statusesOrEmpty());
        return ApiResponse.success(AdminHomeBannerListResponse.of(banners, LocalDateTime.now()));
    }

    @Override
    @GetMapping("/past")
    public ApiResponse<PageResponse<AdminHomeBannerSummaryResponse>> getPastBanners(
        AdminApiUser apiUser,
        @Valid @ModelAttribute AdminHomeBannerCategoryParams params,
        @Valid @ModelAttribute PageParams pageParams
    ) {
        // 지난 배너는 전부 노출 종료라 어느 시각으로 판정해도 같지만, 상태 계산 경로를 하나로 둔다
        LocalDateTime now = LocalDateTime.now();
        PageResult<HomeBanner> result =
            adminHomeService.getPastBanners(params.categoriesOrEmpty(), pageParams.toOffset());
        return ApiResponse.success(
            PageResponse.from(result, banner -> AdminHomeBannerSummaryResponse.of(banner, now)));
    }

    @Override
    @GetMapping("/{bannerId}")
    public ApiResponse<AdminHomeBannerDetailResponse> getBanner(
        AdminApiUser apiUser,
        @PathVariable Long bannerId
    ) {
        return ApiResponse.success(adminHomeBannerDetailUseCase.getBanner(bannerId));
    }

    @Override
    @PostMapping
    public ApiResponse<Void> createBanner(
        AdminApiUser apiUser,
        @Valid @RequestBody AdminHomeBannerWriteRequest request
    ) {
        adminHomeBannerWriteUseCase.create(request.toCreateCommand());
        return ApiResponse.success();
    }

    // 순서 변경(/order)보다 뒤에 두지 않아도 된다. 글자 그대로인 경로가 {bannerId}보다 먼저 맞춰진다
    @Override
    @PutMapping("/{bannerId}")
    public ApiResponse<Void> updateBanner(
        AdminApiUser apiUser,
        @PathVariable Long bannerId,
        @Valid @RequestBody AdminHomeBannerWriteRequest request
    ) {
        adminHomeBannerWriteUseCase.update(bannerId, request.toUpdateCommand());
        return ApiResponse.success();
    }

    @Override
    @DeleteMapping("/{bannerId}")
    public ApiResponse<Void> deleteBanner(AdminApiUser apiUser, @PathVariable Long bannerId) {
        adminHomeService.deleteBanner(bannerId);
        return ApiResponse.success();
    }

    @Override
    @PutMapping("/order")
    public ApiResponse<Void> updateBannerOrders(
        AdminApiUser apiUser,
        @Valid @RequestBody AdminHomeBannerOrderUpdateRequest request
    ) {
        adminHomeService.updateBannerOrders(request.bannerIds());
        return ApiResponse.success();
    }
}
