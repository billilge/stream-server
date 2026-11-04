package kr.ac.kookmin.stream.api.admin.event.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.event.event.request.EventCreateRequest;
import kr.ac.kookmin.stream.api.admin.event.event.request.EventPublishRequest;
import kr.ac.kookmin.stream.api.admin.event.event.request.EventSearchParams;
import kr.ac.kookmin.stream.api.admin.event.event.request.EventUpdateRequest;
import kr.ac.kookmin.stream.api.admin.event.event.response.AdminEventDetailResponse;
import kr.ac.kookmin.stream.api.admin.event.event.response.AdminEventListItemResponse;
import kr.ac.kookmin.stream.api.admin.event.event.response.EventCreateResponse;
import kr.ac.kookmin.stream.api.admin.event.event.response.EventPublishResponse;
import kr.ac.kookmin.stream.api.admin.event.event.response.EventUpdateResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 행사 API의 문서 명세. 구현은 {@link AdminEventController}가 맡는다.
 * <p>
 * 접근은 {@code ADMIN} role이면 된다. 부서 제한은 두지 않았다.
 */
@Tag(name = "행사 관리", description = "운영진 행사 등록·조회·수정·공개 전환·삭제")
public interface AdminEventApi {

    /** 행사 등록. 작성자는 토큰의 관리자로 정해진다. */
    @Operation(summary = "행사 등록",
        description = "행사와 신청서 질문을 등록한다. 이미지는 업로드를 마친 파일 ID로 넘기며, 존재하지 않는 파일이 하나라도 "
            + "있으면 등록하지 않는다. 새 행사는 비공개로 만들어진다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(
        type = EventErrorCode.class,
        codes = {"EVENT_INVALID_PERIOD", "EVENT_INVALID_CAPACITY", "EVENT_INVALID_QUESTION"}
    )
    @ApiErrorCode(type = FileErrorCode.class, codes = {"FILE_NOT_FOUND"})
    ApiResponse<EventCreateResponse> createEvent(AdminApiUser apiUser, EventCreateRequest request);

    /** 행사 목록. 비공개 행사도 함께 내려준다. */
    @Operation(summary = "행사 목록 조회",
        description = "삭제되지 않은 행사를 공개 여부와 상관없이 최근 등록 순으로 페이지 단위 조회한다. keyword로 제목을 검색한다. "
            + "모집 상태는 조회 시점의 신청 기간·잔여 정원으로 계산한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    ApiResponse<PageResponse<AdminEventListItemResponse>> getEvents(
        AdminApiUser apiUser,
        @ParameterObject EventSearchParams params,
        @ParameterObject PageParams pageParams
    );

    /** 행사 상세. 수정 화면의 초기값으로 쓴다. */
    @Operation(summary = "행사 상세 조회",
        description = "행사 정보와 신청서 질문 전체를 조회한다. 비공개 행사도 조회할 수 있다.")
    @ApiErrorCode(type = EventErrorCode.class, codes = {"EVENT_NOT_FOUND"})
    ApiResponse<AdminEventDetailResponse> getEvent(AdminApiUser apiUser, Long eventId);

    /** 행사 수정. 요청의 수정 시각이 저장된 값과 다르면 거부한다. */
    @Operation(summary = "행사 수정",
        description = "행사 내용과 신청서 질문을 통째로 바꾼다. 수정 화면이 행사를 읽은 뒤 다른 관리자가 먼저 고쳤다면"
            + "(수정 시각 불일치) 409로 거부한다. 신청 내역(취소 포함)이 있는 행사는 질문을 바꿀 수 없고, "
            + "선착순 정원은 현재 신청자 수보다 줄일 수 없다. 공개 여부는 바꾸지 않는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(
        type = EventErrorCode.class,
        codes = {
            "EVENT_NOT_FOUND", "EVENT_INVALID_PERIOD", "EVENT_INVALID_CAPACITY", "EVENT_INVALID_QUESTION",
            "EVENT_QUESTIONS_LOCKED", "EVENT_CAPACITY_BELOW_APPLICANTS", "EVENT_UPDATE_CONFLICT"
        }
    )
    @ApiErrorCode(type = FileErrorCode.class, codes = {"FILE_NOT_FOUND"})
    ApiResponse<EventUpdateResponse> updateEvent(AdminApiUser apiUser, Long eventId, EventUpdateRequest request);

    /** 공개 상태 변경. 공개한 행사만 학생 앱 목록·상세에 보인다. */
    @Operation(summary = "행사 공개 상태 변경",
        description = "행사를 학생 앱에 공개하거나 비공개로 돌린다. 이미 그 상태여도 성공으로 응답한다. "
            + "비공개로 돌려도 기존 신청 내역은 유지된다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = EventErrorCode.class, codes = {"EVENT_NOT_FOUND"})
    ApiResponse<EventPublishResponse> changePublished(AdminApiUser apiUser, Long eventId, EventPublishRequest request);

    /** 행사 삭제. 소프트 삭제라 데이터는 남지만 어디에도 보이지 않는다. */
    @Operation(summary = "행사 삭제",
        description = "행사를 삭제한다. 신청자가 있어도 지울 수 있으며, 행사와 그 신청 내역은 운영진 화면과 "
            + "학생 앱(내 신청 내역 포함)에서 보이지 않게 된다. 데이터는 지우지 않고 복구 기능은 없다.")
    @ApiErrorCode(type = EventErrorCode.class, codes = {"EVENT_NOT_FOUND"})
    ApiResponse<Void> deleteEvent(AdminApiUser apiUser, Long eventId);
}
