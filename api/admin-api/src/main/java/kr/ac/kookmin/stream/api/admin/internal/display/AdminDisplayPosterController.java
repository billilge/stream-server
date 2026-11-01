package kr.ac.kookmin.stream.api.admin.internal.display;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.internal.display.request.DisplayPosterActiveRequest;
import kr.ac.kookmin.stream.api.admin.internal.display.request.DisplayPosterCreateRequest;
import kr.ac.kookmin.stream.api.admin.internal.display.request.DisplayPosterUpdateRequest;
import kr.ac.kookmin.stream.api.admin.internal.display.response.DisplayPosterActiveResponse;
import kr.ac.kookmin.stream.api.admin.internal.display.response.DisplayPosterCreateResponse;
import kr.ac.kookmin.stream.api.admin.internal.display.response.DisplayPosterListResponse;
import kr.ac.kookmin.stream.api.admin.internal.display.response.DisplayPosterUpdateResponse;
import kr.ac.kookmin.stream.api.admin.internal.display.usecase.AdminDisplayPosterWriteUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.file.service.FileService;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPoster;
import kr.ac.kookmin.stream.internal.domain.display.service.AdminDisplayPosterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/display/posters")
@RequiredArgsConstructor
public class AdminDisplayPosterController implements AdminDisplayPosterApi {

    private final AdminDisplayPosterService adminDisplayPosterService;
    private final AdminDisplayPosterWriteUseCase adminDisplayPosterWriteUseCase;
    private final FileService fileService;

    @Override
    @GetMapping
    public ApiResponse<DisplayPosterListResponse> getPosters(AdminApiUser apiUser) {
        List<DisplayPoster> posters = adminDisplayPosterService.getPosters();
        Map<Long, File> imagesById = fileService.findAllByIdIn(
            posters.stream().map(DisplayPoster::getImageFileId).toList());

        return ApiResponse.success(DisplayPosterListResponse.of(posters, imagesById));
    }

    @Override
    @PostMapping
    public ApiResponse<DisplayPosterCreateResponse> createPoster(
        AdminApiUser apiUser,
        @Valid @RequestBody DisplayPosterCreateRequest request
    ) {
        DisplayPoster poster = adminDisplayPosterWriteUseCase.create(request.toCommand());
        return ApiResponse.success(DisplayPosterCreateResponse.from(poster));
    }

    @Override
    @PutMapping("/{posterId}")
    public ApiResponse<DisplayPosterUpdateResponse> updatePoster(
        AdminApiUser apiUser,
        @PathVariable Long posterId,
        @Valid @RequestBody DisplayPosterUpdateRequest request
    ) {
        DisplayPoster poster = adminDisplayPosterWriteUseCase.update(posterId, request.toCommand());
        return ApiResponse.success(DisplayPosterUpdateResponse.from(poster));
    }

    @Override
    @DeleteMapping("/{posterId}")
    public ApiResponse<Void> deletePoster(AdminApiUser apiUser, @PathVariable Long posterId) {
        adminDisplayPosterWriteUseCase.delete(posterId);
        return ApiResponse.success();
    }

    @Override
    @PatchMapping("/{posterId}/active")
    public ApiResponse<DisplayPosterActiveResponse> changeActive(
        AdminApiUser apiUser,
        @PathVariable Long posterId,
        @Valid @RequestBody DisplayPosterActiveRequest request
    ) {
        DisplayPoster poster = adminDisplayPosterService.changeActive(posterId, request.active());
        return ApiResponse.success(DisplayPosterActiveResponse.from(poster));
    }
}
