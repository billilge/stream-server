package kr.ac.kookmin.stream.api.admin.event.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.event.event.request.EventApplicationSearchParams;
import kr.ac.kookmin.stream.api.admin.event.event.response.AdminEventApplicationDetailResponse;
import kr.ac.kookmin.stream.api.admin.event.event.response.AdminEventApplicationListResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 행사 신청 현황 API의 문서 명세. 구현은 {@link AdminEventApplicationController}가 맡는다.
 * <p>
 * 접근은 {@code ADMIN} role이면 된다. 부서 제한은 두지 않았다.
 */
@Tag(name = "행사 신청 현황", description = "운영진 행사 신청자 목록·상세 조회")
public interface AdminEventApplicationApi {

    /** 신청 현황. 한 줄에 신청자 정보와 질문별 답변을 함께 내려준다. */
    @Operation(summary = "행사 신청 현황 조회",
        description = "행사의 신청을 신청 순으로 페이지 단위 조회한다. 취소된 신청도 함께 내려가며, keyword로 이름·학번을, "
            + "status로 신청 상태를 거른다. 답변은 질문 순서대로 담기고, 선택형은 고른 선택지 문구를 쉼표로 잇는다. "
            + "탈퇴한 회원의 신청은 이름·학번이 비어 있다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = EventErrorCode.class, codes = {"EVENT_NOT_FOUND", "EVENT_INVALID_APPLICATION_STATUS"})
    ApiResponse<AdminEventApplicationListResponse> getApplications(
        AdminApiUser apiUser,
        Long eventId,
        @ParameterObject EventApplicationSearchParams params,
        @ParameterObject PageParams pageParams
    );

    /** 신청자 상세. 표에서 잘린 답변을 질문 전체와 함께 확인한다. */
    @Operation(summary = "행사 신청자 상세 조회",
        description = "신청 한 건의 신청자 정보와, 행사 질문 전체에 이 신청의 답변을 붙여 조회한다. 답하지 않은 질문도 "
            + "빈 답변으로 함께 내려간다. 다른 행사의 신청이면 찾을 수 없다.")
    @ApiErrorCode(type = EventErrorCode.class, codes = {"EVENT_NOT_FOUND", "APPLICATION_NOT_FOUND"})
    ApiResponse<AdminEventApplicationDetailResponse> getApplication(AdminApiUser apiUser, Long eventId, Long applicationId);
}
