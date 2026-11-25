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
            + "마감되거나 미게시된 대상은 누를 수 없는 '신청하기'가 되므로 뺀다. "
            + "사물함 배정 상태는 신청이 시작된 가장 최근 회차 하나로 정한다 — 그 회차에 신청했으면 이용 기간 중 "
            + "ASSIGNED, 이용 기간이 끝나면 EXPIRED이고, 신청하지 않았으면 신청 기간 중 APPLICABLE, 그 밖에는 "
            + "NOT_APPLIED다. 회차가 없어도 NOT_APPLIED다. "
            + "단, 운영진이 신청 시작 전 회차에 미리 배정한 칸이 있으면 최근 회차가 ASSIGNED가 아닐 때 "
            + "그 칸으로 ASSIGNED를 준다.")
    ApiResponse<HomeResponse> getHome(AppApiUser apiUser);
}
