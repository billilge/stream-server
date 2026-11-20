package kr.ac.kookmin.stream.api.ai.welfare.notice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import kr.ac.kookmin.stream.api.ai.welfare.notice.response.AiNoticeResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;

/**
 * AI 서버용 공지 API의 문서 명세. 구현은 {@link AiNoticeController}가 맡는다.
 */
@Tag(name = "AI 서버 — 공지", description = "챗봇 인덱싱용 조회 (X-Internal-Token)")
public interface AiNoticeApi {

    @Operation(summary = "공지 전체 조회",
        description = "학생 앱에 보이는 공지 전체를 본문까지 한 번에 내려준다. 챗봇이 검색 인덱스를 만들 때 쓴다. "
            + "작성자·이미지·첨부 파일은 내려주지 않는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"UNAUTHORIZED"})
    ApiResponse<List<AiNoticeResponse>> getNotices();
}
