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

    /** 피드백 목록. 답변이 공개된 피드백만 year/round로 필터링하고 페이지 단위로 조회한다. */
    @Operation(summary = "피드백 목록 조회",
        description = "답변을 공개한 회차에서 답변이 달린 피드백만 연도(year)·회차(round)로 필터링해 페이지 단위로 조회한다. "
            + "year를 생략하면 전체 연도, round를 생략하면 그 안의 전체 회차를 대상으로 한다. "
            + "답변 전 질문은 내려가지 않는다(본인 질문은 내 피드백 목록에서 본다). "
            + "회차의 답변 공개 시각 → 답변 시각(answeredAt) 최신순으로 정렬한다.")
    @ApiErrorCode(type = FeedbackErrorCode.class, codes = {"INVALID_FEEDBACK_ROUND"})
    ApiResponse<PageResponse<FeedbackResponse>> getFeedbacks(
        AppApiUser apiUser,
        Integer year,
        Integer round,
        @ParameterObject PageParams pageParams
    );

    /** 내 작성내역. 로그인 회원이 쓴 질문을 답변 여부와 관계없이 조회한다. */
    @Operation(summary = "내 피드백 목록 조회",
        description = "로그인 회원이 쓴 질문을 답변이 없는 질문까지 포함해 질문 시각 최신순으로 페이지 단위 조회한다. "
            + "답변(answer·answeredAt)은 답변을 공개한 회차에서만 내려가고, 공개 전이면 null이다.")
    ApiResponse<PageResponse<FeedbackResponse>> getMyFeedbacks(AppApiUser apiUser, @ParameterObject PageParams pageParams);

    /** 피드백 상세. 답변이 공개된 피드백이거나 본인 질문일 때만 조회된다. */
    @Operation(summary = "피드백 상세 조회",
        description = "답변이 공개된 피드백이거나 본인이 쓴 질문일 때만 조회되고, 그 밖에는 FEEDBACK_NOT_FOUND다. "
            + "본인 질문이라도 답변(answer·answeredAt)은 답변을 공개한 회차에서만 내려가고, 공개 전이면 null이다.")
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
