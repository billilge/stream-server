package kr.ac.kookmin.stream.api.admin.welfare.fee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.ApiErrorCode;
import kr.ac.kookmin.stream.ApiResponse;
import kr.ac.kookmin.stream.PageParams;
import kr.ac.kookmin.stream.PageResponse;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.welfare.fee.request.FeeAmountUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.fee.request.FeeLinkUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.fee.request.FeeStatusUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.AdminFeeSearchResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.FeeAmountUpdateResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.FeeLinkUpdateResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.FeeStatusUpdateResponse;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;

/**
 * 운영진 학생회비 API의 문서 명세. 구현은 {@link AdminFeeController}가 맡는다.
 * <p>
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다. 경로 매핑과
 * 파라미터 바인딩(@{@code ModelAttribute}, @{@code RequestBody} 등), 부서 권한(@{@code RequireDepartment})은
 * 구현체에 둔다.
 */
@Tag(name = "학생회비 관리", description = "운영진 학생회비 납부 확인·설정")
public interface AdminFeeApi {

    /** 납부 확인 요청 목록. 상태·키워드로 필터링하고 페이지네이션한다. */
    @Operation(summary = "납부 확인 요청 목록 조회",
        description = "학생회비 납부 확인 요청을 상태(status)와 이름·학번 키워드(keyword)로 필터링해 페이지 단위로 조회한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = FeeErrorCode.class, codes = {"INVALID_TRANSFER_STATUS"})
    ApiResponse<PageResponse<AdminFeeSearchResponse>> getRequests(
        AdminApiUser apiUser,
        String status,
        String keyword,
        PageParams pageParams
    );

    /** 납부 확인 요청 처리. 승인/반려로 상태를 바꾸고 납부자 명부에 반영한다. */
    @Operation(summary = "납부 확인 요청 처리",
        description = "납부 확인 요청을 승인 또는 반려 처리하고 납부자 명부를 갱신한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(
        type = FeeErrorCode.class,
        codes = {"INVALID_TRANSFER_STATUS", "FEE_REQUEST_NOT_FOUND", "FEE_REQUEST_ALREADY_REVIEWED"}
    )
    @ApiErrorCode(type = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"})
    ApiResponse<FeeStatusUpdateResponse> updateStatus(
        AdminApiUser apiUser,
        Long transferStatusId,
        FeeStatusUpdateRequest request
    );

    /** 송금 링크(은행·계좌번호) 설정. */
    @Operation(summary = "송금 링크 설정",
        description = "학생회비 송금에 쓰이는 은행과 계좌번호를 설정한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    ApiResponse<FeeLinkUpdateResponse> updateLink(
        AdminApiUser apiUser,
        FeeLinkUpdateRequest request
    );

    /** 학생회비 금액 설정. */
    @Operation(summary = "학생회비 금액 설정",
        description = "학생회비 송금 금액을 설정한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    ApiResponse<FeeAmountUpdateResponse> updateAmount(
        AdminApiUser apiUser,
        FeeAmountUpdateRequest request
    );
}
