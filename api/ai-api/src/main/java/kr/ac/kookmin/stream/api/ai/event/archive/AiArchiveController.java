package kr.ac.kookmin.stream.api.ai.event.archive;

import java.util.List;
import kr.ac.kookmin.stream.api.ai.event.archive.response.AiArchiveResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveSummary;
import kr.ac.kookmin.stream.event.domain.archive.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ai/archives")
@RequiredArgsConstructor
public class AiArchiveController implements AiArchiveApi {

    private final ArchiveService archiveService;

    /**
     * 학생 목록(연도·검색어 없이 = 전체)으로 대상을 정하고, 본문은 한 건씩 상세로 가져온다.
     * 목록 요약에는 본문이 없다. 아카이빙은 학기에 수십 건 수준이라 한 건씩 읽어도 부담이 없다.
     */
    @Override
    @GetMapping
    public ApiResponse<List<AiArchiveResponse>> getArchives() {
        List<AiArchiveResponse> archives = archiveService.getArchives(null, null).stream()
            .map(ArchiveSummary::archiveId)
            .map(archiveService::getArchive)
            .map(AiArchiveResponse::from)
            .toList();

        return ApiResponse.success(archives);
    }
}
