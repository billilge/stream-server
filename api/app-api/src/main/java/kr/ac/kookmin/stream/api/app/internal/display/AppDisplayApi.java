package kr.ac.kookmin.stream.api.app.internal.display;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.app.internal.display.response.DisplayScreenResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;

/**
 * 오프라인 디스플레이 화면 API의 문서 명세. 구현은 {@link AppDisplayController}가 맡는다.
 * <p>
 * 디스플레이 기기는 로그인하지 않으므로 인증 없이 연다({@code PublicEndpoints.DISPLAY}). 학생 앱 전용 경로
 * ({@code /v1/app/**})가 아니라 {@code /v1/display}에 두는 것도 그래서다.
 */
@Tag(name = "디스플레이", description = "오프라인 디스플레이 화면 조회")
public interface AppDisplayApi {

    /** 디스플레이 화면. 포스터·학사 일정·물품 재고를 한 번에 내려준다. */
    @Operation(summary = "디스플레이 화면 조회",
        description = "활성화된 행사 포스터(등록 순), 오늘(KST) 앞뒤 3일의 학사 일정, 빌릴게 물품 재고(물품 등록 순)를 한 번에 "
            + "조회한다. schedules는 날짜 오름차순 7개이고, 일정이 없는 날은 titles가 빈 배열이다. 여러 날에 걸친 일정은 기간에 "
            + "든 날짜마다 들어간다. 재고가 0인 물품도 내려준다. 로그인 없이 호출한다.")
    ApiResponse<DisplayScreenResponse> getScreen();
}
