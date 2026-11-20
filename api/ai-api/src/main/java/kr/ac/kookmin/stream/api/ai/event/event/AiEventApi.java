package kr.ac.kookmin.stream.api.ai.event.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import kr.ac.kookmin.stream.api.ai.event.event.response.AiEventResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;

/**
 * AI 서버용 행사 API의 문서 명세. 구현은 {@link AiEventController}가 맡는다.
 */
@Tag(name = "AI 서버 — 행사", description = "챗봇 인덱싱용 조회 (X-Internal-Token)")
public interface AiEventApi {

    @Operation(summary = "행사 전체 조회",
        description = "학생 앱에 보이는(게시된) 행사 전체를 소개글까지 한 번에 내려준다. 챗봇이 검색 인덱스를 만들 때 쓴다. "
            + "모집 인원·신청 현황처럼 계속 바뀌는 값은 넣지 않는다 — 챗봇이 질문할 때 학생 API로 따로 조회한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"UNAUTHORIZED"})
    ApiResponse<List<AiEventResponse>> getEvents();
}
