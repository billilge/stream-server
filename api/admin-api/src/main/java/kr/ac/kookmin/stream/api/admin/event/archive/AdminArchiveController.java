package kr.ac.kookmin.stream.api.admin.event.archive;

import jakarta.validation.Valid;
import java.util.Map;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.event.archive.request.ArchiveExposureChangeRequest;
import kr.ac.kookmin.stream.api.admin.event.archive.request.ArchiveSearchParams;
import kr.ac.kookmin.stream.api.admin.event.archive.request.ArchiveWriteRequest;
import kr.ac.kookmin.stream.api.admin.event.archive.response.AdminArchiveDetailResponse;
import kr.ac.kookmin.stream.api.admin.event.archive.response.AdminArchiveListItemResponse;
import kr.ac.kookmin.stream.api.admin.event.archive.usecase.AdminArchiveDetailUseCase;
import kr.ac.kookmin.stream.api.admin.event.archive.usecase.AdminArchiveWriteUseCase;
import kr.ac.kookmin.stream.api.admin.event.archive.usecase.ArchiveWithRelatedEvent;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.archive.domain.Archive;
import kr.ac.kookmin.stream.event.domain.archive.service.AdminArchiveService;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 목록은 정렬 기준이 달라 {@link AdminArchiveService}가, 상세는 학생 앱과 내용이 같아 {@link ArchiveService}가 맡는다
 * ({@code coding-style.md} 2-7절).
 */
@RestController
@RequestMapping("/v1/admin/archives")
@RequiredArgsConstructor
public class AdminArchiveController implements AdminArchiveApi {

    private final AdminArchiveService adminArchiveService;
    private final AdminArchiveDetailUseCase adminArchiveDetailUseCase;
    private final AdminArchiveWriteUseCase adminArchiveWriteUseCase;
    private final FileService fileService;

    @Override
    @GetMapping
    public ApiResponse<PageResponse<AdminArchiveListItemResponse>> getArchives(
        AdminApiUser apiUser,
        @Valid @ModelAttribute ArchiveSearchParams searchParams,
        @Valid @ModelAttribute PageParams pageParams
    ) {
        PageResult<Archive> result = adminArchiveService.getArchives(
            searchParams.keyword(), searchParams.isExposed(), pageParams.toOffset());

        return ApiResponse.success(PageResponse.from(result, AdminArchiveListItemResponse::from));
    }

    @Override
    @GetMapping("/{archiveId}")
    public ApiResponse<AdminArchiveDetailResponse> getArchive(
        AdminApiUser apiUser,
        @PathVariable Long archiveId
    ) {
        ArchiveWithRelatedEvent archive = adminArchiveDetailUseCase.getArchive(archiveId);
        Map<Long, File> imagesById = fileService.findAllByIdIn(archive.detail().imageIds());

        return ApiResponse.success(
            AdminArchiveDetailResponse.of(archive.detail(), archive.relatedEventTitle(), imagesById));
    }

    @Override
    @PostMapping
    public ApiResponse<Void> createArchive(
        AdminApiUser apiUser,
        @Valid @RequestBody ArchiveWriteRequest request
    ) {
        adminArchiveWriteUseCase.create(request.toCommand());
        return ApiResponse.success();
    }

    @Override
    @PutMapping("/{archiveId}")
    public ApiResponse<Void> updateArchive(
        AdminApiUser apiUser,
        @PathVariable Long archiveId,
        @Valid @RequestBody ArchiveWriteRequest request
    ) {
        adminArchiveWriteUseCase.update(archiveId, request.toCommand());
        return ApiResponse.success();
    }

    @Override
    @DeleteMapping("/{archiveId}")
    public ApiResponse<Void> deleteArchive(AdminApiUser apiUser, @PathVariable Long archiveId) {
        adminArchiveService.delete(archiveId);
        return ApiResponse.success();
    }

    @Override
    @PatchMapping("/exposure")
    public ApiResponse<Void> changeExposure(
        AdminApiUser apiUser,
        @Valid @RequestBody ArchiveExposureChangeRequest request
    ) {
        adminArchiveService.changeExposure(request.archiveIds(), request.isExposed());
        return ApiResponse.success();
    }
}
