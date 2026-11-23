package kr.ac.kookmin.stream.api.ai.welfare.feedback;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import kr.ac.kookmin.stream.api.ai.welfare.feedback.response.AiFeedbackResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;

/**
 * AI 서버용 열린피드백 API의 문서 명세. 구현은 {@link AiFeedbackController}가 맡는다.
 */
@Tag(name = "AI 서버 — 열린피드백", description = "챗봇 인덱싱용 조회 (X-Internal-Token)")
public interface AiFeedbackApi {

    @Operation(summary = "공개된 열린피드백 답변 전체 조회",
        description = "답변이 공개된 회차의 질문·답변 전체를 한 번에 내려준다. 챗봇이 검색 인덱스를 만들 때 쓴다. "
            + "질문한 학생·답변한 운영진은 내려주지 않는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"UNAUTHORIZED"})
    ApiResponse<List<AiFeedbackResponse>> getFeedbacks();
}
