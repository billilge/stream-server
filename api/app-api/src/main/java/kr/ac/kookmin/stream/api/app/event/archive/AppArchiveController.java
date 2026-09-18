package kr.ac.kookmin.stream.api.app.event.archive;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.ApiErrorCode;
import kr.ac.kookmin.stream.ApiResponse;
import kr.ac.kookmin.stream.api.app.event.archive.request.ArchiveListRequest;
import kr.ac.kookmin.stream.api.app.event.archive.response.ArchiveDetailResponse;
import kr.ac.kookmin.stream.api.app.event.archive.response.ArchiveListResponse;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveErrorCode;
import kr.ac.kookmin.stream.event.domain.archive.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/archives")
@RequiredArgsConstructor
public class AppArchiveController {

    private final ArchiveService archiveService;

    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @GetMapping
    public ApiResponse<ArchiveListResponse> getArchives(@Valid @ModelAttribute ArchiveListRequest request) {
        return ApiResponse.success(ArchiveListResponse.from(archiveService.getArchives(request.year())));
    }

    @ApiErrorCode(type = ArchiveErrorCode.class, codes = {"ARCHIVE_NOT_FOUND"})
    @GetMapping("/{archiveId}")
    public ApiResponse<ArchiveDetailResponse> getArchive(@PathVariable Long archiveId) {
        return ApiResponse.success(ArchiveDetailResponse.from(archiveService.getArchive(archiveId)));
    }
}
