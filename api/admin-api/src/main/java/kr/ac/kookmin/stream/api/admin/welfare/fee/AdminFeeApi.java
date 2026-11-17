package kr.ac.kookmin.stream.api.admin.welfare.fee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.excel.ExcelFile;
import kr.ac.kookmin.stream.api.admin.welfare.fee.request.FeeAccountUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.fee.request.FeeStatusUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.AdminFeeSearchResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.AdminPayerSearchResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.FeeAccountResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.FeeStatusUpdateResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

/**
 * 운영진 학생회비 API의 문서 명세. 구현은 {@link AdminFeeController}가 맡는다.
 * <p>
 * 계좌와 학기당 회비는 한 화면에서 같이 저장하므로 하나의 엔드포인트({@code /account})로 묶여 있다.
 */
@Tag(name = "학생회비 관리", description = "운영진 학생회비 납부 확인·설정")
public interface AdminFeeApi {

    /** 납부 확인 요청 목록. 납부를 요청한 회원만 나오고, 상태를 지정하지 않으면 납부 완료는 빠진다. */
    @Operation(summary = "학생회비 납부 확인 요청 목록 조회",
        description = "이름·학번 검색과 납부 상태 필터로 납부를 요청한 회원을 조회한다. "
            + "한 번도 요청하지 않은 회원은 나오지 않는다. 금액은 요청 시점에 학생에게 안내한 값이다. "
            + "상태를 지정하지 않으면 처리할 요청(PENDING·REJECTED)만 나오고 납부 완료(PAID)는 빠진다. "
            + "납부 완료한 회원은 납부자 목록 조회에서 보고, 잘못 처리한 납부 완료를 되돌릴 때는 status=PAID로 찾는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = FeeErrorCode.class, codes = {"INVALID_TRANSFER_STATUS"})
    ApiResponse<PageResponse<AdminFeeSearchResponse>> getRequests(
        AdminApiUser apiUser,
        String status,
        String keyword,
        @ParameterObject PageParams pageParams
    );

    /** 납부 요청 목록 엑셀 내보내기. 목록과 같은 조건의 요청을 모두 담는다. */
    @Operation(summary = "학생회비 납부 확인 요청 엑셀 내보내기",
        description = "납부 요청 목록과 같은 이름·학번 검색과 납부 상태 필터로 요청을 페이지 없이 모두 xlsx 파일로 내려준다. "
            + "열은 이름·학번·학과·요청일시·남은 학기·요청 금액·납부일·납부 상태·관리자 안내다. 값이 없으면 빈 칸이고, "
            + "납부일은 납부 완료한 요청에만 있다. 파일 이름은 Content-Disposition 헤더의 filename*(UTF-8)로 내려간다. "
            + "에러는 다른 API처럼 JSON으로 내려간다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "xlsx 파일",
        content = @Content(mediaType = ExcelFile.MEDIA_TYPE, schema = @Schema(type = "string", format = "binary")))
    @ApiErrorCode(type = FeeErrorCode.class, codes = {"INVALID_TRANSFER_STATUS"})
    ResponseEntity<byte[]> exportRequests(AdminApiUser apiUser, String status, String keyword);

    /** 납부 상태 처리. 이미 처리한 요청도 다시 바꿀 수 있다. */
    @Operation(summary = "학생회비 납부 상태 수정",
        description = "납부 상태를 바꾼다. 잘못 처리한 경우를 되돌릴 수 있도록 이미 처리한 요청도 다시 바꿀 수 있다. "
            + "확인필요(REJECTED)로 바꿀 때는 학생에게 보여줄 반려 사유(255자 이하)가 필요하고, "
            + "다른 상태로 바꿀 때는 반려 사유를 보내도 저장하지 않는다. "
            + "확인필요로 돌려받은 학생은 반려 사유를 보고 학기 인증부터 다시 낼 수 있다. "
            + "회원의 최신 요청만 바꿀 수 있다 — 지난 요청은 이력이다. "
            + "드물게 학생이 새 요청을 내는 순간과 겹치면 늦게 저장되는 쪽이 FEE_REQUEST_ALREADY_REQUESTED로 실패할 수 있다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = FeeErrorCode.class, codes = {"INVALID_REJECT_REASON", "FEE_REQUEST_NOT_FOUND",
        "FEE_REQUEST_NOT_LATEST", "FEE_REQUEST_ALREADY_REQUESTED"})
    @ApiErrorCode(type = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"})
    ApiResponse<FeeStatusUpdateResponse> updateStatus(
        AdminApiUser apiUser,
        Long studentTransferStatusId,
        FeeStatusUpdateRequest request
    );

    /** 납부자 목록(명부). 납부 확인 요청 목록과 달리 앱에 가입하지 않은 레거시 이관 납부자도 나온다. */
    @Operation(summary = "학생회비 납부자 목록 조회",
        description = "이름·학번 검색으로 학생회비 납부자 명부를 이름순으로 조회한다. 납부가 확인(PAID)된 회원과 "
            + "앱에 가입하지 않은 레거시 이관 납부자가 함께 나오고, 레거시 납부자는 memberId가 null이다(학번도 null일 수 있다). "
            + "사물함 배정에서 학생을 고를 수 있도록 부서 제한이 없다. "
            + "memberId가 null인 행은 배정 API에 넘길 회원 식별자가 없어 사물함 배정과 관리자 지정에 쓸 수 없으므로 "
            + "화면에서 선택을 막아야 한다. 이 행은 학부(department)도 null이다. "
            + "isCouncilRegistered는 이미 관리자 관리 명단에 올라 있는지로, 앱 가입 여부인 isRegistered와 다르다 — "
            + "관리자 추가 화면은 isCouncilRegistered가 true인 행의 선택을 막는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    ApiResponse<PageResponse<AdminPayerSearchResponse>> getPayers(
        AdminApiUser apiUser,
        String keyword,
        @ParameterObject PageParams pageParams
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
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = FeeErrorCode.class, codes = {"INVALID_BANK"})
    ApiResponse<FeeAccountResponse> updateAccount(AdminApiUser apiUser, FeeAccountUpdateRequest request);
}
