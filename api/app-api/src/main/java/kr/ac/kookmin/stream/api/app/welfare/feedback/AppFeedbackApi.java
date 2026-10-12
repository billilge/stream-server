package kr.ac.kookmin.stream.api.app.welfare.feedback;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.feedback.request.FeedbackCreateRequest;
import kr.ac.kookmin.stream.api.app.welfare.feedback.response.FeedbackResponse;
import kr.ac.kookmin.stream.api.app.welfare.feedback.response.FeedbackRoundsResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 학생 앱 열린피드백 API의 문서 명세. 구현은 {@link AppFeedbackController}가 맡는다.
 * <p>
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다. 경로 매핑과
 * 파라미터 바인딩(@{@code RequestParam}, @{@code PathVariable}, @{@code ModelAttribute} 등)은 구현체에 둔다.
 */
@Tag(name = "열린피드백", description = "학생 앱 열린피드백 질문 등록·조회")
public interface AppFeedbackApi {

    /** 피드백 목록. year/round로 필터링하고 페이지 단위로 조회한다. */
    @Operation(summary = "피드백 목록 조회",
        description = "연도(year)·회차(round)로 필터링해 열린피드백 목록을 페이지 단위로 조회한다. "
            + "year를 생략하면 전체 연도, round를 생략하면 그 안의 전체 회차를 대상으로 한다.")
    @ApiErrorCode(type = FeedbackErrorCode.class, codes = {"INVALID_FEEDBACK_ROUND"})
    ApiResponse<PageResponse<FeedbackResponse>> getFeedbacks(
        AppApiUser apiUser,
        Integer year,
        Integer round,
        @ParameterObject PageParams pageParams
    );

    /** 피드백 상세. */
    @Operation(summary = "피드백 상세 조회")
    @ApiErrorCode(type = FeedbackErrorCode.class, codes = {"FEEDBACK_NOT_FOUND"})
    ApiResponse<FeedbackResponse> getFeedback(AppApiUser apiUser, Long feedbackId);

    /** 피드백 질문 등록. 회차는 현재 열려 있는 회차로 서버가 자동 배정한다. */
    @Operation(summary = "피드백 질문 등록",
        description = "질문을 등록한다. 연도·회차는 현재 접수 기간이 열려 있는 회차로 서버가 자동 배정한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = FeedbackErrorCode.class, codes = {"FEEDBACK_NOT_OPEN"})
    ApiResponse<FeedbackResponse> createFeedback(AppApiUser apiUser, FeedbackCreateRequest request);

    /** 목록 화면의 연도 드롭다운·회차 칩을 그리기 위한 필터 옵션. */
    @Operation(summary = "피드백 회차 필터 조회",
        description = "year를 생략하면 현재 연도 기준으로, 회차가 하나라도 존재했던 전체 연도 목록과 그 연도에 실제로 존재하는 회차 목록을 함께 내려준다.")
    ApiResponse<FeedbackRoundsResponse> getFeedbackRounds(AppApiUser apiUser, Integer year);
}
