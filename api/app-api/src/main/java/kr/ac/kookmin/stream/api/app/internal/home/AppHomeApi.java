package kr.ac.kookmin.stream.api.app.internal.home;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.internal.home.response.HomeResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;

/**
 * 학생 앱 홈 화면 API의 문서 명세. 구현은 {@link AppHomeController}가 맡는다.
 * <p>
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다.
 */
@Tag(name = "홈", description = "학생 앱 홈 화면 조회")
public interface AppHomeApi {

    @Operation(summary = "홈 화면 조회",
        description = "홈 배너, 신청 섹션, 대여 중인 물품, 신청한 행사, 학생회비 납부 상태, "
            + "열린피드백 작성 수, 사물함 배정 상태를 한 번에 조회한다. "
            + "대여와 신청한 행사는 미리보기 2건과 전체 수를 함께 준다. "
            + "신청 섹션은 운영진이 노출로 설정한 것 중 지금 신청할 수 있는 것만 담긴다 — "
            + "마감되거나 미게시된 대상은 누를 수 없는 '신청하기'가 되므로 뺀다.")
    ApiResponse<HomeResponse> getHome(AppApiUser apiUser);
}
