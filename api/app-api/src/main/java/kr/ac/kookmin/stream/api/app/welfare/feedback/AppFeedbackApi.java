package kr.ac.kookmin.stream.api.app.welfare.feedback;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.feedback.response.FeedbackResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackErrorCode;

/**
 * 학생 앱 열린피드백 API의 문서 명세. 구현은 {@link AppFeedbackController}가 맡는다.
 * <p>
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다. 경로 매핑과
 * 파라미터 바인딩(@{@code RequestParam}, @{@code PathVariable}, @{@code ModelAttribute} 등)은 구현체에 둔다.
 */
@Tag(name = "열린피드백", description = "학생 앱 열린피드백 질문 등록·조회")
public interface AppFeedbackApi {

    /** 피드백 상세. */
    @Operation(summary = "피드백 상세 조회")
    @ApiErrorCode(type = FeedbackErrorCode.class, codes = {"FEEDBACK_NOT_FOUND"})
    ApiResponse<FeedbackResponse> getFeedback(AppApiUser apiUser, Long feedbackId);
}
