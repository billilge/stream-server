package kr.ac.kookmin.stream.api.app.welfare.fee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.ApiErrorCode;
import kr.ac.kookmin.stream.ApiResponse;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.fee.request.FeeConfirmRequest;
import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeResponse;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;

/**
 * 학생 앱 학생회비 API의 문서 명세. 구현은 {@link AppFeeController}가 맡는다.
 * <p>
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다. 경로 매핑과
 * 파라미터 바인딩(@{@code RequestParam}, @{@code RequestBody} 등)은 구현체에 둔다.
 */
@Tag(name = "학생회비", description = "학생 앱 학생회비 납부")
public interface AppFeeApi {

    /** 내 학생회비 납부 상태와 송금 링크. 학년·학기를 지정하면 해당 학기의 송금 링크를 내려준다. */
    @Operation(summary = "내 학생회비 상태 조회",
        description = "로그인한 학생의 납부 상태와 함께 지정한 학년·학기의 송금 링크를 조회한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = FeeErrorCode.class, codes = {"FEE_TRANSFER_LINK_INVALID"})
    ApiResponse<FeeResponse> getFeeByMemberId(
        AppApiUser apiUser,
        Integer grade,
        Integer semester
    );

    /** 납부 확인 요청. 송금 후 운영진 확인을 요청하는 상태로 전환한다. */
    @Operation(summary = "학생회비 납부 확인 요청",
        description = "송금을 마친 학생이 운영진의 납부 확인을 요청한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = FeeErrorCode.class, codes = {"FEE_TRANSFER_LINK_INVALID"})
    ApiResponse<FeeResponse> requestConfirmation(
        AppApiUser apiUser,
        FeeConfirmRequest request
    );
}
