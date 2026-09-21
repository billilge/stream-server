package kr.ac.kookmin.stream.api.app.event.archive;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.app.event.archive.request.ArchiveListParams;
import kr.ac.kookmin.stream.api.app.event.archive.response.ArchiveDetailResponse;
import kr.ac.kookmin.stream.api.app.event.archive.response.ArchiveListResponse;
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
public class AppArchiveController implements AppArchiveApi {

    private final ArchiveService archiveService;

    /**
     * 목록과 연도는 서로 독립적인 조회다. 연도 버튼은 필터와 무관하게 전체가 보여야 하므로 각각 조회해 응답에서 합친다.
     */
    @Override
    @GetMapping
    public ApiResponse<ArchiveListResponse> getArchives(@Valid @ModelAttribute ArchiveListParams params) {
        return ApiResponse.success(ArchiveListResponse.of(
            archiveService.getArchives(params.year()),
            archiveService.getYears()
        ));
    }

    @Override
    @GetMapping("/{archiveId}")
    public ApiResponse<ArchiveDetailResponse> getArchive(@PathVariable Long archiveId) {
        return ApiResponse.success(ArchiveDetailResponse.from(archiveService.getArchive(archiveId)));
    }
}
