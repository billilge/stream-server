package kr.ac.kookmin.stream.api.app.event.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.CursorSliceResponse;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.event.event.request.EventApplicationListParams;
import kr.ac.kookmin.stream.api.app.event.event.request.EventApplyRequest;
import kr.ac.kookmin.stream.api.app.event.event.request.EventListParams;
import kr.ac.kookmin.stream.api.app.event.event.response.EventApplicationDetailResponse;
import kr.ac.kookmin.stream.api.app.event.event.response.EventApplicationListItemResponse;
import kr.ac.kookmin.stream.api.app.event.event.response.EventApplyResponse;
import kr.ac.kookmin.stream.api.app.event.event.response.EventDetailResponse;
import kr.ac.kookmin.stream.api.app.event.event.response.EventFormResponse;
import kr.ac.kookmin.stream.api.app.event.event.response.EventListItemResponse;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 학생 앱 행사 API의 문서 명세. 구현은 {@link AppEventController}가 맡는다.
 * <p>
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다. 경로 매핑과
 * 파라미터 바인딩(@{@code ModelAttribute}, @{@code PathVariable} 등)은 구현체에 둔다.
 */
@Tag(name = "행사", description = "학생 앱 행사 조회·신청")
public interface AppEventApi {

    /** 게시된 행사 목록. 커서 기반 페이지네이션이며 모집 상태로 필터링한다. */
    @Operation(summary = "행사 목록 조회",
        description = "게시된 행사를 커서 기반으로 조회한다. recruitStatus로 모집 상태를 필터링하고, cursor/size로 다음 페이지를 넘긴다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = EventErrorCode.class, codes = {"EVENT_INVALID_RECRUIT_STATUS", "EVENT_INVALID_CURSOR"})
    ApiResponse<CursorSliceResponse<EventListItemResponse>> getEvents(@ParameterObject EventListParams params);

    /** 행사 상세. */
    @Operation(summary = "행사 상세 조회")
    @ApiErrorCode(type = EventErrorCode.class, codes = {"EVENT_NOT_FOUND"})
    ApiResponse<EventDetailResponse> getEvent(Long eventId);

    /** 행사 신청서 양식. 모집 중인 행사의 질문 목록을 내려준다. */
    @Operation(summary = "행사 신청서 양식 조회",
        description = "모집 중인 행사의 신청서 질문 목록을 조회한다.")
    @ApiErrorCode(type = EventErrorCode.class, codes = {"EVENT_NOT_FOUND", "CAPACITY_FULL", "ALREADY_CLOSED"})
    ApiResponse<EventFormResponse> getApplicationForm(Long eventId);

    /** 행사 신청. 신청서 답변과 함께 신청을 접수한다. */
    @Operation(summary = "행사 신청",
        description = "신청서 답변을 제출해 행사 신청을 접수한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(
        type = EventErrorCode.class,
        codes = {"EVENT_NOT_FOUND", "CAPACITY_FULL", "ALREADY_CLOSED", "ALREADY_APPLIED", "INVALID_ANSWER"}
    )
    ApiResponse<EventApplyResponse> apply(
        AppApiUser apiUser,
        Long eventId,
        EventApplyRequest request
    );

    /** 내 행사 신청 내역 목록. */
    // 커서 Base64 디코딩 실패는 CursorCodec이 INVALID_INPUT으로, 그 뒤 형식 오류는 EVENT_INVALID_CURSOR로 걸린다
    @Operation(summary = "내 행사 신청 내역 목록 조회",
        description = "로그인한 학생의 행사 신청 내역을 커서 기반으로 조회한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = EventErrorCode.class, codes = {"EVENT_INVALID_CURSOR"})
    ApiResponse<CursorSliceResponse<EventApplicationListItemResponse>> getMyApplications(
        AppApiUser apiUser,
        @ParameterObject EventApplicationListParams params
    );

    /** 내 행사 신청 상세. 행사의 질문 전체에 이 신청의 답변을 붙여 내려준다. */
    @Operation(summary = "내 행사 신청 상세 조회",
        description = "내 행사 신청 하나의 상세를, 행사 질문 전체에 답변을 붙여 조회한다.")
    @ApiErrorCode(type = EventErrorCode.class, codes = {"APPLICATION_NOT_FOUND", "EVENT_NOT_FOUND"})
    ApiResponse<EventApplicationDetailResponse> getMyApplication(
        AppApiUser apiUser,
        Long applicationId
    );

    /** 행사 신청 취소. 신청 기록은 지우지 않고 상태만 CANCELED로 바꾼다. */
    @Operation(summary = "행사 신청 취소",
        description = "신청 기록은 남기고 상태만 취소로 바꾼다.")
    @ApiErrorCode(
        type = EventErrorCode.class,
        codes = {"APPLICATION_NOT_FOUND", "EVENT_NOT_FOUND", "ALREADY_CANCELED", "CANCEL_DEADLINE_PASSED"}
    )
    ApiResponse<Void> cancelApplication(
        AppApiUser apiUser,
        Long applicationId
    );
}
