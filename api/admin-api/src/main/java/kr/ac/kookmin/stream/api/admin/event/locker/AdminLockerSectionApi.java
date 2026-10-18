package kr.ac.kookmin.stream.api.admin.event.locker;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.event.locker.request.LockerPeriodParams;
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
@Tag(name = "사물함 구역 관리", description = "운영진 사물함 구역 목록·상세 조회")
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
}
