package kr.ac.kookmin.stream.api.admin.event.locker;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.event.locker.request.LockerPeriodCreateRequest;
import kr.ac.kookmin.stream.api.admin.event.locker.request.LockerPeriodUpdateRequest;
import kr.ac.kookmin.stream.api.admin.event.locker.response.AdminLockerPeriodDetailResponse;
import kr.ac.kookmin.stream.api.admin.event.locker.response.AdminLockerPeriodListResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode;

/**
 * 운영진 사물함 운영 회차 API의 문서 명세. 구현은 {@link AdminLockerPeriodController}가 맡는다.
 * <p>
 * 접근은 {@code ADMIN} role이면 된다. 부서 제한은 두지 않았다.
 * <p>
 * 회차 상태(`periodStatus`)는 저장하지 않고 서버 현재 시각으로 계산한다 — 신청 시작 전이면 {@code UPCOMING},
 * 사용 종료일이 지났으면 {@code ENDED}, 그 사이는 {@code IN_PROGRESS}다.
 */
@Tag(name = "사물함 회차 관리", description = "운영진 사물함 운영 회차 조회·등록·수정·삭제")
public interface AdminLockerPeriodApi {

    /** 회차 목록과 진행 중인 회차. 학생 앱 조회와 달리 아직 게시하지 않은 회차도 함께 보여준다. */
    @Operation(summary = "사물함 운영 회차 목록 조회",
        description = "운영 회차를 신청 시작 일시 최신순으로 모두 조회한다. 진행 중인 회차는 `currentPeriod`에도 따로 담기며 "
            + "없으면 null이다. 삭제한 회차는 빠진다.")
    ApiResponse<AdminLockerPeriodListResponse> getLockerPeriods(AdminApiUser apiUser);

    /** 회차 상세. 수정 화면의 기존 값을 채우는 데 쓴다. */
    @Operation(summary = "사물함 운영 회차 상세 조회",
        description = "운영 회차 한 건을 조회한다. 연도·학기는 수정할 수 없지만 화면 표시를 위해 따로 내려준다. "
            + "삭제한 회차는 찾을 수 없는 것으로 응답한다.")
    @ApiErrorCode(type = LockerErrorCode.class, codes = {"LOCKER_PERIOD_NOT_FOUND"})
    ApiResponse<AdminLockerPeriodDetailResponse> getLockerPeriod(AdminApiUser apiUser, Long lockerPeriodId);

    /** 회차 등록. 등록한 회차는 바로 학생 앱에서 신청할 수 있는 상태가 된다. */
    @Operation(summary = "사물함 운영 회차 등록",
        description = "운영 회차를 등록한다. 회차 이름은 연도와 학기로 서버가 만든다(예: 2027-1학기). 신청 기간은 "
            + "시작일 00:00:00부터 마감일 23:59:59까지로 저장한다. 같은 학기는 하나만 등록할 수 있고, 회차 기간"
            + "(신청 시작일 ~ 사용 종료일)은 다른 회차와 겹칠 수 없다. "
            + "신청 마감일과 사용 시작일의 선후는 보지 않는다 — 사용을 시작한 뒤 남은 자리를 추가 모집할 수 있다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = LockerErrorCode.class,
        codes = {"INVALID_LOCKER_PERIOD_RANGE", "LOCKER_PERIOD_SEMESTER_DUPLICATED", "LOCKER_PERIOD_OVERLAPPED"})
    ApiResponse<Void> createLockerPeriod(AdminApiUser apiUser, LockerPeriodCreateRequest request);

    /** 회차 수정. 전체 교체이므로 바꾸지 않는 값도 원래 값 그대로 보낸다. */
    @Operation(summary = "사물함 운영 회차 수정",
        description = "운영 회차의 신청·사용 기간을 통째로 바꾼다. 연도·학기와 게시 여부는 바뀌지 않는다. "
            + "종료된 회차는 수정할 수 없고, 진행 중인 회차는 신청 시작일을 다른 값으로 바꿀 수 없다(원래 값을 그대로 보내면 된다). "
            + "바꾼 기간이 다른 회차와 겹쳐도 거부한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = LockerErrorCode.class,
        codes = {"LOCKER_PERIOD_NOT_FOUND", "INVALID_LOCKER_PERIOD_RANGE", "LOCKER_PERIOD_ALREADY_ENDED",
            "LOCKER_PERIOD_APPLY_ALREADY_STARTED", "LOCKER_PERIOD_OVERLAPPED"})
    ApiResponse<Void> updateLockerPeriod(AdminApiUser apiUser, Long lockerPeriodId, LockerPeriodUpdateRequest request);

    /** 회차 삭제. 소프트 삭제라 행은 남고 조회에서만 빠진다. */
    @Operation(summary = "사물함 운영 회차 삭제",
        description = "운영 회차를 삭제한다. 신청이 한 건이라도 있으면 삭제하지 않는다. 데이터는 남기고 조회에서만 빠진다.")
    @ApiErrorCode(type = LockerErrorCode.class, codes = {"LOCKER_PERIOD_NOT_FOUND", "LOCKER_PERIOD_HAS_APPLICATIONS"})
    ApiResponse<Void> deleteLockerPeriod(AdminApiUser apiUser, Long lockerPeriodId);
}
