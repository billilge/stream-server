package kr.ac.kookmin.stream.api.admin.welfare.feedback;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.welfare.feedback.request.FeedbackAnswerSaveRequest;
import kr.ac.kookmin.stream.api.admin.welfare.feedback.request.FeedbackRoundCreateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.feedback.response.FeedbackRoundDetailResponse;
import kr.ac.kookmin.stream.api.admin.welfare.feedback.response.FeedbackRoundResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 열린피드백 회차 API의 문서 명세. 구현은 {@link AdminFeedbackRoundController}가 맡는다.
 * <p>
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다. 경로 매핑과
 * 파라미터 바인딩(@{@code RequestParam}, @{@code PathVariable}, @{@code RequestBody} 등)은 구현체에 둔다.
 */
@Tag(name = "열린피드백 회차 관리", description = "운영진 열린피드백 회차 등록·조회와 답변 작성·공개")
public interface AdminFeedbackRoundApi {

    /** 관리 화면 상단 요약. 답변을 아직 공개하지 않은 회차 중 지금 진행 중인 회차를 내려준다. */
    @Operation(summary = "진행 중 회차 조회",
        description = "답변을 아직 공개하지 않은 회차 중 접수가 시작된 회차(질문접수중·답변작성중)를 시작일이 가장 늦은 것으로 "
            + "조회하고, 그런 회차가 없으면 시작일이 가장 가까운 오픈예정 회차를 조회한다. "
            + "둘 다 없으면 data가 null이다.")
    ApiResponse<FeedbackRoundResponse> getCurrentRound(AdminApiUser apiUser);

    /** 회차 목록. 연도·상태로 필터링하고 페이지 단위로 조회한다. */
    @Operation(summary = "피드백 회차 목록 조회",
        description = "연도(year)와 상태(status)로 필터링해 회차를 최신순(연도 내림차순 → 회차 내림차순)으로 조회한다. "
            + "상태는 저장하지 않고 접수 기간과 답변 공개 여부로 서버가 판정한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = FeedbackErrorCode.class, codes = {"INVALID_FEEDBACK_ROUND_STATUS"})
    ApiResponse<PageResponse<FeedbackRoundResponse>> getRounds(
        AdminApiUser apiUser,
        Integer year,
        String status,
        @ParameterObject PageParams pageParams
    );

    /** 회차 등록. 접수 기간만 받고 연도·회차 번호는 서버가 배정한다. */
    @Operation(summary = "피드백 회차 등록",
        description = "질문 접수 기간(날짜)만 받아 회차를 연다. 시각은 시작일 00:00:00 ~ 마감일 23:59:59로 채우고, "
            + "연도는 시작일 기준, 회차 번호는 그 연도의 최대 + 1로 배정한다. 다른 회차와 접수 기간이 겹치면 409다. "
            + "동시에 등록해 같은 회차 번호가 배정되면 늦은 쪽이 409이고, 다시 요청하면 다음 번호로 등록된다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(
        type = FeedbackErrorCode.class,
        codes = {"INVALID_FEEDBACK_ROUND_PERIOD", "FEEDBACK_ROUND_OVERLAPPED", "FEEDBACK_ROUND_ALREADY_EXISTS"}
    )
    ApiResponse<FeedbackRoundResponse> createRound(AdminApiUser apiUser, FeedbackRoundCreateRequest request);

    /** 회차 답변 보기 모달. 그 회차의 질문 전체를 답변과 함께 내려준다. */
    @Operation(summary = "회차 답변 내역 조회",
        description = "그 회차에 접수된 질문 전체를 질문 시각 오름차순으로 조회한다. 답변이 없는 질문도 포함된다.")
    @ApiErrorCode(type = FeedbackErrorCode.class, codes = {"FEEDBACK_ROUND_NOT_FOUND"})
    ApiResponse<FeedbackRoundDetailResponse> getRoundAnswers(AdminApiUser apiUser, Long roundId);

    /** 답변 임시저장. 저장만 하고 학생에게는 보이지 않는다. */
    @Operation(summary = "피드백 답변 일괄 등록·수정",
        description = "회차 단위로 답변을 저장한다. 보낸 것만 반영하고 이미 답변이 있는 질문은 덮어쓴다. "
            + "여기서 저장한 답변은 관리자에게만 보이고, 답변 공개 API를 불러야 학생 앱에 나타난다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(
        type = FeedbackErrorCode.class,
        codes = {"FEEDBACK_NOT_IN_ROUND", "FEEDBACK_ROUND_NOT_FOUND", "FEEDBACK_NOT_FOUND"}
    )
    ApiResponse<FeedbackRoundDetailResponse> saveAnswers(
        AdminApiUser apiUser, Long roundId, FeedbackAnswerSaveRequest request);

    /** 답변 공개. 그 회차의 답변을 학생 앱에 한꺼번에 띄운다. */
    @Operation(summary = "피드백 답변 공개",
        description = "그 회차에 작성된 답변을 학생에게 공개한다. 공개는 회차 단위이고, 질문 접수가 끝난 회차(답변작성중)만 "
            + "공개할 수 있다(오픈예정·질문접수중이면 409). "
            + "이미 공개한 회차를 다시 불러도 공개 시각을 바꾸지 않고 성공으로 응답한다(멱등).")
    @ApiErrorCode(
        type = FeedbackErrorCode.class,
        codes = {"FEEDBACK_ANSWER_NOT_FOUND", "FEEDBACK_ROUND_NOT_FOUND", "FEEDBACK_QUESTION_PERIOD_NOT_ENDED"}
    )
    ApiResponse<FeedbackRoundDetailResponse> publishAnswers(AdminApiUser apiUser, Long roundId);
}
