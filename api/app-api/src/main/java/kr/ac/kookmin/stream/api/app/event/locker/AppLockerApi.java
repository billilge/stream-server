package kr.ac.kookmin.stream.api.app.event.locker;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.event.locker.request.LockerSectionListParams;
import kr.ac.kookmin.stream.api.app.event.locker.response.LockerLayoutResponse;
import kr.ac.kookmin.stream.api.app.event.locker.response.LockerSectionListResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 학생 앱 사물함 API의 문서 명세. 구현은 {@link AppLockerController}가 맡는다.
 * <p>
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다. 경로 매핑과
 * 파라미터 바인딩(@{@code ModelAttribute}, @{@code PathVariable} 등)은 구현체에 둔다.
 */
@Tag(name = "사물함", description = "학생 앱 사물함 구역·배치 조회")
public interface AppLockerApi {

    /** 구역별 전체·선택 가능 사물함 수와 표시 상태. */
    @Operation(summary = "사물함 구역 목록 조회",
        description = "운영 회차의 구역별 전체 사물함 수와 현재 선택 가능한 사물함 수를 조회한다. "
            + "사용 중지된 사물함과 해당 회차에 이미 신청된 사물함은 선택 가능 수에서 빠진다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = LockerErrorCode.class, codes = {"LOCKER_PERIOD_NOT_FOUND"})
    ApiResponse<LockerSectionListResponse> getSections(
        AppApiUser apiUser,
        @ParameterObject LockerSectionListParams params
    );

    /** 구역에 속한 사물함의 배치 정보와 선택 가능 여부. */
    @Operation(summary = "사물함 구역 상세 조회",
        description = "구역에 속한 사물함의 배치도 위치와 선택 가능 여부를 조회한다. "
            + "사물함이 사용 가능한 상태이고 해당 회차에 신청되지 않은 경우에만 선택할 수 있다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = LockerErrorCode.class, codes = {"LOCKER_PERIOD_NOT_FOUND", "LOCKER_SECTION_NOT_FOUND"})
    ApiResponse<LockerLayoutResponse> getSectionLockers(
        AppApiUser apiUser,
        Long sectionId,
        @ParameterObject LockerSectionListParams params
    );
}
