package kr.ac.kookmin.stream.api.ai.welfare.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import kr.ac.kookmin.stream.api.ai.welfare.item.response.AiItemResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;

/**
 * AI 서버용 빌릴게 물품 API의 문서 명세. 구현은 {@link AiItemController}가 맡는다.
 */
@Tag(name = "AI 서버 — 빌릴게 물품", description = "챗봇 재고 조회용 (X-Internal-Token)")
public interface AiItemApi {

    @Operation(summary = "물품 전체 조회",
        description = "학생 앱에 보이는 물품 전체와 지금 남은 수량을 한 번에 내려준다. 챗봇이 재고 질문에 답할 때 부른다. "
            + "재고는 학생마다 달라지는 값이 아니라 학생 JWT 없이 내부 토큰만으로 조회한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"UNAUTHORIZED"})
    ApiResponse<List<AiItemResponse>> getItems();
}
