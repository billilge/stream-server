package kr.ac.kookmin.stream.api.app.internal.display;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.app.internal.display.request.DisplayCalendarScheduleListParams;
import kr.ac.kookmin.stream.api.app.internal.display.response.DisplayCalendarScheduleListResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 학생 앱 학사 일정 API의 문서 명세. 구현은 {@link AppDisplayCalendarScheduleController}가 맡는다.
 * <p>
 * 운영진이 디스플레이 관리에서 등록한 학사 일정을 그대로 보여준다. 홈 주간 카드와 학사 일정 상세(한 달 달력)가 함께 쓴다.
 */
@Tag(name = "학사 일정", description = "학생 앱 학사 일정 조회")
public interface AppDisplayCalendarScheduleApi {

    /** 학사 일정. 홈 주간 카드는 한 주, 학사 일정 상세는 달력에 보이는 범위로 조회한다. */
    @Operation(summary = "학사 일정 조회",
        description = "기간이 startDate~endDate와 하루라도 겹치는 일정을 시작일, 등록 순으로 조회한다. 범위 밖에서 시작했어도 "
            + "범위에 걸치면 포함한다(예: 12/21~1/15 일정은 1월 조회에도 나온다). 하루 일정은 startDate와 endDate가 같다. "
            + "endDate가 startDate보다 앞서면 400이다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = DisplayErrorCode.class, codes = {"INVALID_DISPLAY_SCHEDULE_PERIOD"})
    ApiResponse<DisplayCalendarScheduleListResponse> getSchedules(@ParameterObject DisplayCalendarScheduleListParams params);
}
