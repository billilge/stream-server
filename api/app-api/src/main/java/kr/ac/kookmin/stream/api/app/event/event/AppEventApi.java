package kr.ac.kookmin.stream.api.app.event.event;

import kr.ac.kookmin.stream.ApiErrorCode;
import kr.ac.kookmin.stream.ApiResponse;
import kr.ac.kookmin.stream.CursorSliceResponse;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.event.event.request.EventApplicationListParams;
import kr.ac.kookmin.stream.api.app.event.event.response.EventApplicationDetailResponse;
import kr.ac.kookmin.stream.api.app.event.event.response.EventApplicationListItemResponse;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;

/**
 * 학생 앱 행사 API의 문서 명세. 구현은 {@link AppEventController}가 맡는다.
 * <p>
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다. 경로 매핑과
 * 파라미터 바인딩(@{@code ModelAttribute}, @{@code PathVariable} 등)은 구현체에 둔다.
 */
public interface AppEventApi {

    /** 내 행사 신청 내역 목록. */
    // 커서 Base64 디코딩 실패는 CursorCodec이 INVALID_INPUT으로, 그 뒤 형식 오류는 EVENT_INVALID_CURSOR로 걸린다
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = EventErrorCode.class, codes = {"EVENT_INVALID_CURSOR"})
    ApiResponse<CursorSliceResponse<EventApplicationListItemResponse>> getMyApplications(
        AppApiUser apiUser,
        EventApplicationListParams params
    );

    /** 내 행사 신청 상세. 행사의 질문 전체에 이 신청의 답변을 붙여 내려준다. */
    @ApiErrorCode(type = EventErrorCode.class, codes = {"APPLICATION_NOT_FOUND", "EVENT_NOT_FOUND"})
    ApiResponse<EventApplicationDetailResponse> getMyApplication(
        AppApiUser apiUser,
        Long applicationId
    );

    /** 행사 신청 취소. 신청 기록은 지우지 않고 상태만 CANCELED로 바꾼다. */
    @ApiErrorCode(
        type = EventErrorCode.class,
        codes = {"APPLICATION_NOT_FOUND", "EVENT_NOT_FOUND", "ALREADY_CANCELED", "CANCEL_DEADLINE_PASSED"}
    )
    ApiResponse<Void> cancelApplication(
        AppApiUser apiUser,
        Long applicationId
    );
}
