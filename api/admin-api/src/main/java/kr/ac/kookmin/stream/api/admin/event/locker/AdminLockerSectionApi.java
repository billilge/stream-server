package kr.ac.kookmin.stream.api.admin.event.locker;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.event.locker.request.LockerPeriodParams;
import kr.ac.kookmin.stream.api.admin.event.locker.request.LockerStatusChangeRequest;
import kr.ac.kookmin.stream.api.admin.event.locker.response.AdminLockerSectionDetailResponse;
import kr.ac.kookmin.stream.api.admin.event.locker.response.AdminLockerSectionListResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 사물함 구역 API의 문서 명세. 구현은 {@link AdminLockerSectionController}가 맡는다.
 * <p>
 * 접근은 {@code ADMIN} role이면 된다. 부서 제한은 두지 않았다.
 * <p>
 * 구역은 운영 회차에 묶여 있지 않다. 요청의 회차는 "어느 회차의 배정 현황을 볼지"를 정하는 기준이다.
 */
@Tag(name = "사물함 구역 관리", description = "운영진 사물함 구역 목록·상세 조회와 사물함 상태 변경")
public interface AdminLockerSectionApi {

    /** 구역별 전체·선택 가능·배정 사물함 수. */
    @Operation(summary = "사물함 구역 목록 조회",
        description = "운영 회차를 기준으로 구역별 전체 사물함 수, 선택 가능한 사물함 수, 배정된 사물함 수를 조회한다. "
            + "사용 중지된 사물함과 그 회차에 배정된 사물함은 선택 가능 수에서 빠진다. "
            + "배정 수는 배정된 뒤 사용 중지된 사물함도 센다. 구역은 식별자 오름차순으로 반환한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = LockerErrorCode.class, codes = {"LOCKER_PERIOD_NOT_FOUND"})
    ApiResponse<AdminLockerSectionListResponse> getLockerSections(
        AdminApiUser apiUser,
        @ParameterObject LockerPeriodParams params
    );

    /** 구역의 칸 배치·사진과 사물함별 상태. */
    @Operation(summary = "사물함 구역 상세 조회",
        description = "운영 회차를 기준으로 구역의 칸 배치 구조(layout), 구역 사진, 사물함별 상태를 조회한다. "
            + "layout은 저장된 원문을 그대로 돌려주며 칸 번호는 lockers[].lockerNumber와 같다. "
            + "배치 구조를 아직 등록하지 않은 구역이면 layout이 null이고 photos가 빈 배열이다. "
            + "사물함 상태는 물리 상태와 그 회차의 배정 여부를 합친 값이고, 배정된 뒤 사용 중지된 칸은 ASSIGNED다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = LockerErrorCode.class, codes = {"LOCKER_PERIOD_NOT_FOUND", "LOCKER_SECTION_NOT_FOUND"})
    ApiResponse<AdminLockerSectionDetailResponse> getLockerSection(
        AdminApiUser apiUser,
        Long sectionId,
        @ParameterObject LockerPeriodParams params
    );

    /** 구역 안의 사물함 여러 개를 같은 상태로 바꾼다. */
    @Operation(summary = "사물함 상태 일괄 변경",
        description = "구역 안의 사물함 여러 개를 AVAILABLE(선택가능) 또는 DISABLED(선택불가)로 한 번에 바꾼다. "
            + "하나라도 바꿀 수 없으면 아무것도 바뀌지 않는다. "
            + "사용 기간이 끝나지 않은 회차에 배정된 사물함은 이용 중인 학생이 있으므로 DISABLED로 바꿀 수 없고, "
            + "AVAILABLE로 되돌리는 것은 막지 않는다. "
            + "구역 상세가 내려주는 ASSIGNED는 신청으로 생기는 표시용 값이라 이 경로로 바꿀 수 없다. "
            + "응답 본문이 없으므로 바뀐 결과는 구역 상세를 다시 조회해 받는다. "
            + "이용 중 검사와 학생 신청이 겹치면 서로 다른 테이블에 써서 둘 다 성공할 수 있고, "
            + "그때는 배정된 칸이 사용 중지된 채로 남는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = LockerErrorCode.class, codes = {"LOCKER_NOT_IN_SECTION", "LOCKER_IN_USE"})
    ApiResponse<Void> changeLockerStatus(
        AdminApiUser apiUser,
        Long sectionId,
        LockerStatusChangeRequest request
    );
}
