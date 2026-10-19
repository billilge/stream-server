package kr.ac.kookmin.stream.api.admin.welfare.fee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.welfare.fee.request.FeeAccountUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.fee.request.FeeStatusUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.AdminFeeSearchResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.FeeAccountResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.FeeStatusUpdateResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 학생회비 API의 문서 명세. 구현은 {@link AdminFeeController}가 맡는다.
 * <p>
 * 계좌와 학기당 회비는 한 화면에서 같이 저장하므로 하나의 엔드포인트(`/account`)로 묶여 있다.
 */
@Tag(name = "학생회비 (운영진)", description = "학생회비 납부 관리")
public interface AdminFeeApi {

    /** 납부 요청 목록. 납부를 요청한 회원만 나온다. */
    @Operation(summary = "학생회비 납부자 목록 조회",
        description = "이름·학번 검색과 납부 상태 필터로 납부를 요청한 회원을 조회한다. "
            + "한 번도 요청하지 않은 회원은 나오지 않는다. 금액은 요청 시점에 학생에게 안내한 값이다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = FeeErrorCode.class, codes = {"INVALID_TRANSFER_STATUS"})
    ApiResponse<PageResponse<AdminFeeSearchResponse>> getRequests(
        AdminApiUser apiUser,
        String status,
        String keyword,
        @ParameterObject PageParams pageParams
    );

    /** 납부 상태 처리. 이미 처리한 요청도 다시 바꿀 수 있다. */
    @Operation(summary = "학생회비 납부 상태 수정",
        description = "납부 상태를 바꾼다. 잘못 처리한 경우를 되돌릴 수 있도록 이미 처리한 요청도 다시 바꿀 수 있다. "
            + "확인필요(REJECTED)로 바꿀 때는 학생에게 보여줄 안내 문구가 필요하다. "
            + "확인필요로 돌려받은 학생은 안내 문구를 보고 학기 인증부터 다시 낼 수 있다.")
    @ApiErrorCode(type = FeeErrorCode.class,
        codes = {"INVALID_TRANSFER_STATUS", "INVALID_ADMIN_NOTE", "FEE_REQUEST_NOT_FOUND"})
    ApiResponse<FeeStatusUpdateResponse> updateStatus(
        AdminApiUser apiUser,
        Long studentTransferStatusId,
        FeeStatusUpdateRequest request
    );

    /** 회비 납부 설정 조회. 수정 폼의 초기값으로 쓴다. */
    @Operation(summary = "회비 납부 계좌 조회",
        description = "설정된 은행·계좌번호·학기당 회비를 조회한다. 아직 설정하지 않았으면 은행과 계좌번호는 null이고 "
            + "학기당 회비는 기본값이 내려간다.")
    ApiResponse<FeeAccountResponse> getAccount(AdminApiUser apiUser);

    /** 회비 납부 설정 변경. 계좌와 학기당 회비를 함께 받는다. */
    @Operation(summary = "회비 납부 계좌 변경",
        description = "은행·계좌번호·학기당 회비를 한 번에 저장한다. 은행은 허용 목록 중 하나여야 하고 "
            + "계좌번호는 하이픈을 제외한 숫자 10~14자리다. 송금 링크는 저장된 값으로 조회 시점에 만들어진다.")
    @ApiErrorCode(type = FeeErrorCode.class,
        codes = {"INVALID_BANK", "INVALID_ACCOUNT_NO", "INVALID_TRANSFER_AMOUNT"})
    ApiResponse<FeeAccountResponse> updateAccount(AdminApiUser apiUser, FeeAccountUpdateRequest request);
}
