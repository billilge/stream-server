package kr.ac.kookmin.stream.api.admin.event.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.event.event.request.EventCreateRequest;
import kr.ac.kookmin.stream.api.admin.event.event.response.EventCreateResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;

/**
 * 운영진 행사 API의 문서 명세. 구현은 {@link AdminEventController}가 맡는다.
 * <p>
 * 접근은 {@code ADMIN} role이면 된다. 부서 제한은 두지 않았다.
 */
@Tag(name = "행사 관리", description = "운영진 행사 등록·조회·수정")
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
}
