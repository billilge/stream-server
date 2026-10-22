package kr.ac.kookmin.stream.api.app.welfare.fee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.fee.request.FeeSubmitRequest;
import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;

/**
 * 학생 앱 학생회비 API의 문서 명세. 구현은 {@link AppFeeController}가 맡는다.
 * <p>
 * 납부는 한 번에 요청한다. 학생은 남은 학기 수로 송금 링크를 받아(조회만, 저장하지 않음) 토스에서 송금한 뒤,
 * 학점이수현황 캡처와 함께 납부를 요청한다. 이때 처음 저장되고 바로 <b>납부확인중</b>이 된다.
 * 운영진이 확인필요로 돌려보내면 학생은 학기 인증부터 다시 낼 수 있다.
 * <p>
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다.
 */
@Tag(name = "학생회비", description = "학생 앱 학생회비 납부")
public interface AppFeeApi {

    /** 내 학생회비 납부 현황. 남은 학기 수를 주면 그 값으로 금액과 송금 링크를 계산한다. */
    @Operation(summary = "내 학생회비 상태 조회",
        description = "로그인한 학생의 납부 상태와 납부 계좌를 조회한다. 아무것도 저장하지 않는다. "
            + "납부 전(UNPAID)이거나 확인필요(REJECTED)인 학생이 remainingSemesters(남은 학기 수, 1~8)를 보내면 "
            + "그 값으로 납부 금액과 송금 링크를 계산해 준다. 납부확인중·납부완료면 요청할 때 남긴 금액만 보여주고 "
            + "송금 링크는 주지 않는다.")
    @ApiErrorCode(type = FeeErrorCode.class, codes = {"INVALID_REMAINING_SEMESTERS", "FEE_ACCOUNT_NOT_FOUND"})
    ApiResponse<FeeResponse> getFeeByMemberId(AppApiUser apiUser, Integer remainingSemesters);

    /** 학생회비 납부 요청. 학기 인증과 송금 완료를 한 번에 받아 바로 납부확인중이 된다. */
    @Operation(summary = "학생회비 납부 요청",
        description = "토스로 송금을 마친 학생이 남은 학기 수와 학점이수현황 캡처를 함께 보내 운영진의 확인을 요청한다. "
            + "요청 시점의 금액(남은 학기 수 × 학기당 회비)을 함께 남긴다. "
            + "처음 내는 학생과 확인필요로 돌려받은 학생만 요청할 수 있고, 돌려받은 학생은 이전 요청을 덮어쓴다.")
    @ApiErrorCode(type = FeeErrorCode.class,
        codes = {"INVALID_REMAINING_SEMESTERS", "INVALID_PROOF_IMAGE", "FEE_ACCOUNT_NOT_FOUND",
            "FEE_REQUEST_ALREADY_REQUESTED"})
    ApiResponse<FeeResponse> submit(AppApiUser apiUser, FeeSubmitRequest request);
}
