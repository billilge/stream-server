package kr.ac.kookmin.stream.api.ai.event.archive;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import kr.ac.kookmin.stream.api.ai.event.archive.response.AiArchiveResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;

/**
 * AI 서버용 아카이빙 API의 문서 명세. 구현은 {@link AiArchiveController}가 맡는다.
 */
@Tag(name = "AI 서버 — 아카이빙", description = "챗봇 인덱싱용 조회 (X-Internal-Token)")
public interface AiArchiveApi {

    @Operation(summary = "아카이빙 전체 조회",
        description = "학생 앱 아카이빙 목록에 보이는 기록 전체를 본문까지 한 번에 내려준다. 챗봇이 검색 인덱스를 만들 때 쓴다. "
            + "이미지는 내려주지 않는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"UNAUTHORIZED"})
    ApiResponse<List<AiArchiveResponse>> getArchives();
}
