package kr.ac.kookmin.stream.api.app.welfare.fee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.fee.request.FeeSubmitRequest;
import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeConfirmResponse;
import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeResponse;
import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeSubmitResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;

/**
 * 학생 앱 학생회비 API의 문서 명세. 구현은 {@link AppFeeController}가 맡는다.
 * <p>
 * 납부는 두 단계다. 납부 정보를 제출해도 상태는 <b>납부 전</b>에 머물고, 토스로 송금한 뒤
 * 송금 확인을 요청해야 <b>납부확인중</b>이 된다. 제출만 하고 이탈한 학생을 운영진이
 * 처리 대기로 보지 않기 위해서다.
 * <p>
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다.
 */
@Tag(name = "학생회비", description = "학생 앱 학생회비 납부")
public interface AppFeeApi {

    /** 내 학생회비 납부 현황. 화면은 feeStatus와 studentTransferStatusId로 분기한다. */
    @Operation(summary = "내 학생회비 상태 조회",
        description = "로그인한 학생의 납부 상태와 납부 계좌, 납부 금액과 송금 링크를 조회한다. "
            + "납부 정보를 제출하기 전이거나 확인필요 상태면 금액과 송금 링크는 내려가지 않는다.")
    @ApiErrorCode(type = FeeErrorCode.class, codes = {"FEE_ACCOUNT_NOT_FOUND"})
    ApiResponse<FeeResponse> getFeeByMemberId(AppApiUser apiUser);

    /** 납부 정보 제출. 상태는 납부 전 그대로이고, 돌려받은 송금 링크로 토스에서 송금한다. */
    @Operation(summary = "학생회비 납부 정보 제출",
        description = "남은 학기 수와 학점이수현황 캡처를 제출하고, 납부 금액이 붙은 토스 송금 링크를 받는다. "
            + "상태는 납부 전 그대로이며 이전에 제출한 값과 이미지는 덮어쓴다. "
            + "확인필요로 처리된 학생은 직접 다시 제출할 수 없고 운영진이 납부 전으로 되돌려야 한다.")
    @ApiErrorCode(type = FeeErrorCode.class,
        codes = {"INVALID_REMAINING_SEMESTERS", "INVALID_PROOF_IMAGE", "FEE_ACCOUNT_NOT_FOUND",
            "FEE_REQUEST_ALREADY_REQUESTED"})
    ApiResponse<FeeSubmitResponse> submit(AppApiUser apiUser, FeeSubmitRequest request);

    /** 송금 확인 요청. 여기서 비로소 납부확인중으로 바뀐다. */
    @Operation(summary = "학생회비 송금 확인 요청",
        description = "송금을 마친 학생이 운영진의 확인을 요청한다. 납부 정보를 제출한 학생만 요청할 수 있다.")
    @ApiErrorCode(type = FeeErrorCode.class,
        codes = {"FEE_REQUEST_NOT_FOUND", "FEE_REQUEST_ALREADY_REQUESTED"})
    ApiResponse<FeeConfirmResponse> requestConfirmation(AppApiUser apiUser);
}
