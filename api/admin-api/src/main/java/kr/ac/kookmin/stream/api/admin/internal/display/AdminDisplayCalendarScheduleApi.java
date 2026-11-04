package kr.ac.kookmin.stream.api.admin.internal.display;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.internal.display.request.DisplayCalendarScheduleCreateRequest;
import kr.ac.kookmin.stream.api.admin.internal.display.request.DisplayCalendarScheduleListParams;
import kr.ac.kookmin.stream.api.admin.internal.display.request.DisplayCalendarScheduleUpdateRequest;
import kr.ac.kookmin.stream.api.admin.internal.display.response.DisplayCalendarScheduleCreateResponse;
import kr.ac.kookmin.stream.api.admin.internal.display.response.DisplayCalendarScheduleListResponse;
import kr.ac.kookmin.stream.api.admin.internal.display.response.DisplayCalendarScheduleUpdateResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 디스플레이 학사 일정 API의 문서 명세. 구현은 {@link AdminDisplayCalendarScheduleController}가 맡는다.
 * <p>
 * 접근은 {@code ADMIN} role이면 된다. 부서 제한은 두지 않았다.
 * <p>
 * 일정 하나는 일정명과 기간(시작일~종료일)이다. 여러 날에 걸친 일정도 한 건이고, 하루 일정은 시작일과 종료일이 같다.
 */
@Tag(name = "디스플레이 학사 일정 관리", description = "운영진 디스플레이 학사 일정 조회·등록·수정·삭제")
public interface AdminDisplayCalendarScheduleApi {

    /** 학사 캘린더. 관리자 화면은 보고 있는 달의 범위로 조회한다. */
    @Operation(summary = "학사 일정 조회",
        description = "기간이 startDate~endDate와 하루라도 겹치는 일정을 시작일, 등록 순으로 조회한다. 범위 밖에서 시작했어도 "
            + "범위에 걸치면 포함한다(예: 9/28~10/2 일정은 10월 조회에도 나온다). endDate가 startDate보다 앞서면 400이다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = DisplayErrorCode.class, codes = {"INVALID_DISPLAY_SCHEDULE_PERIOD"})
    ApiResponse<DisplayCalendarScheduleListResponse> getSchedules(
        AdminApiUser apiUser, @ParameterObject DisplayCalendarScheduleListParams params);

    /** 학사 일정 등록. */
    @Operation(summary = "학사 일정 등록",
        description = "일정명과 기간으로 일정을 등록한다. 종료일이 시작일보다 앞서면 등록하지 않는다. 같은 이름·같은 기간의 일정이 "
            + "있어도 중복 확인 없이 저장한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = DisplayErrorCode.class, codes = {"INVALID_DISPLAY_SCHEDULE_PERIOD"})
    ApiResponse<DisplayCalendarScheduleCreateResponse> createSchedule(
        AdminApiUser apiUser, DisplayCalendarScheduleCreateRequest request);

    /** 학사 일정 수정. 일정명과 기간을 통째로 바꾼다. */
    @Operation(summary = "학사 일정 수정",
        description = "일정명과 기간을 요청 값으로 모두 바꾼다. 종료일이 시작일보다 앞서면 바꾸지 않는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(
        type = DisplayErrorCode.class,
        codes = {"INVALID_DISPLAY_SCHEDULE_PERIOD", "DISPLAY_SCHEDULE_NOT_FOUND"}
    )
    ApiResponse<DisplayCalendarScheduleUpdateResponse> updateSchedule(
        AdminApiUser apiUser, Long scheduleId, DisplayCalendarScheduleUpdateRequest request);

    /** 학사 일정 삭제. 여러 날에 걸친 일정이면 그 기간 전체에서 사라진다. */
    @Operation(summary = "학사 일정 삭제",
        description = "일정 한 건을 DB에서 지운다. 여러 날에 걸친 일정이면 그 기간 전체에서 사라진다.")
    @ApiErrorCode(type = DisplayErrorCode.class, codes = {"DISPLAY_SCHEDULE_NOT_FOUND"})
    ApiResponse<Void> deleteSchedule(AdminApiUser apiUser, Long scheduleId);
}
