package kr.ac.kookmin.stream.api.admin.internal.schedule;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.internal.schedule.request.WorkScheduleRegisterRequest;
import kr.ac.kookmin.stream.api.admin.internal.schedule.request.WorkScheduleUpdateRequest;
import kr.ac.kookmin.stream.api.admin.internal.schedule.request.WorkScheduleWeekParams;
import kr.ac.kookmin.stream.api.admin.internal.schedule.response.WorkScheduleResponse;
import kr.ac.kookmin.stream.api.admin.internal.schedule.response.WorkScheduleWeekResponse;
import kr.ac.kookmin.stream.api.admin.internal.schedule.response.WorkerResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkScheduleErrorCode;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 빌릴게 근무 시간표 API의 문서 명세. 구현은 {@link AdminWorkScheduleController}가 맡는다.
 * <p>
 * 접근은 {@code ADMIN} role이면 된다. 부서 제한은 두지 않았다.
 * <p>
 * 근무는 평일(월~금) 10~17시에 1시간 단위로 서고, 점심시간(12~13시)은 비운다. 한 시간 칸에는 근무자 한 명만 들어간다.
 * 근무자는 학생회 부서가 있는 회원이다. 지난 주(이번 주 월요일보다 앞선 주)의 근무 시간은 바꿀 수 없다.
 */
@Tag(name = "근무 시간표 관리", description = "빌릴게 근무 시간표 조회·등록·수정·삭제와 근무자 목록")
public interface AdminWorkScheduleApi {

    /** 근무 시간표 그리드. 한 주 단위로 조회한다. */
    @Operation(summary = "근무 시간표 조회",
        description = "date가 속한 주(월~금)의 근무 시간을 근무일, 시작 시각 순으로 조회한다. 주는 월요일에 시작하므로 토·일을 "
            + "보내면 그 앞 월요일의 주를 돌려준다. date를 생략하면 서버 시각(KST) 기준 이번 주다. editable이 false면 지난 주라 "
            + "등록·수정·삭제할 수 없다. 탈퇴한 회원의 근무 시간은 name·studentId가 null이다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    ApiResponse<WorkScheduleWeekResponse> getWorkSchedules(
        AdminApiUser apiUser, @ParameterObject WorkScheduleWeekParams params);

    /** 등록·수정 모달의 근무자 드롭다운. */
    @Operation(summary = "근무자 목록 조회",
        description = "근무 시간에 배정할 수 있는 회원(학생회 부서가 있는 회원)을 이름, 같은 이름이면 학번 오름차순으로 조회한다. "
            + "운영진 권한(role)은 보지 않는다.")
    ApiResponse<List<WorkerResponse>> getWorkers(AdminApiUser apiUser);

    /** 근무 시간 등록. 근무자 한 명의 시간대를 여러 개 한 번에 등록한다. */
    @Operation(summary = "근무 시간표 등록",
        description = "근무자 한 명과 시간대 여러 개를 받아 한 번에 등록한다. 하나라도 규칙에 맞지 않으면 아무것도 등록하지 않는다. "
            + "점심시간에 걸치는 시간대(예: 11~14시)는 받지 않으므로 오전·오후 두 건으로 나눠 보낸다. 이미 근무 시간이 있는 칸과 "
            + "겹치거나(근무자가 달라도) 요청 안의 시간대끼리 겹치면 409다. 붙어 있는 시간대는 합치지 않고 보낸 그대로 저장한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"})
    @ApiErrorCode(
        type = WorkScheduleErrorCode.class,
        codes = {"INVALID_WORK_DATE", "INVALID_WORK_HOURS", "PAST_WEEK_NOT_EDITABLE", "WORKER_NOT_FOUND",
            "WORK_SCHEDULE_CONFLICT"}
    )
    ApiResponse<List<WorkScheduleResponse>> registerWorkSchedules(
        AdminApiUser apiUser, WorkScheduleRegisterRequest request);

    /** 근무 시간 수정. 근무자·근무일·시간을 모두 바꾼다. */
    @Operation(summary = "근무 시간표 수정",
        description = "근무 시간 1건의 근무자·근무일·시작·종료를 통째로 바꾼다. 규칙은 등록과 같고, 겹침 검사에서 수정 대상 자신은 "
            + "뺀다. 원래 근무일이나 옮길 근무일이 지난 주면 400이다. 같은 주 안에서 옮기는지는 검사하지 않는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"})
    @ApiErrorCode(
        type = WorkScheduleErrorCode.class,
        codes = {"INVALID_WORK_DATE", "INVALID_WORK_HOURS", "PAST_WEEK_NOT_EDITABLE", "WORK_SCHEDULE_NOT_FOUND",
            "WORKER_NOT_FOUND", "WORK_SCHEDULE_CONFLICT"}
    )
    ApiResponse<WorkScheduleResponse> updateWorkSchedule(
        AdminApiUser apiUser, Long scheduleId, WorkScheduleUpdateRequest request);

    /** 근무 시간 삭제. 행을 지운다. */
    @Operation(summary = "근무 시간표 삭제",
        description = "근무 시간 1건을 삭제한다. 삭제 표시 없이 행을 지운다. 지난 주의 근무 시간이면 400이다.")
    @ApiErrorCode(type = WorkScheduleErrorCode.class, codes = {"PAST_WEEK_NOT_EDITABLE", "WORK_SCHEDULE_NOT_FOUND"})
    ApiResponse<Void> deleteWorkSchedule(AdminApiUser apiUser, Long scheduleId);
}
