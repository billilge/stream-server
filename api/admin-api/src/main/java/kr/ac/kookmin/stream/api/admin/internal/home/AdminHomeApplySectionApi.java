package kr.ac.kookmin.stream.api.admin.internal.home;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.internal.home.request.AdminHomeApplySectionExposureRequest;
import kr.ac.kookmin.stream.api.admin.internal.home.request.AdminHomeApplySectionOrderUpdateRequest;
import kr.ac.kookmin.stream.api.admin.internal.home.request.AdminHomeApplySectionSearchParams;
import kr.ac.kookmin.stream.api.admin.internal.home.response.AdminHomeApplySectionListResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 홈 신청 섹션 API의 문서 명세. 구현은 {@link AdminHomeApplySectionController}가 맡는다.
 * <p>
 * 후보는 저장된 목록이 아니라 지금 신청을 받는 행사·사물함 회차에서 그때그때 나온다. 그래서 설정이 아직
 * 없는 후보도 목록에 들어오고, 노출 여부·순서는 행 식별자가 아니라 {@code targetType}·{@code targetId}로
 * 가리킨다.
 */
@Tag(name = "홈 신청 섹션 관리", description = "운영진 홈 신청 섹션 후보 조회·노출 설정·순서 변경")
public interface AdminHomeApplySectionApi {

    @Operation(summary = "신청 섹션 후보 목록 조회",
        description = "지금 신청을 받는 행사와 사물함 운영 회차를 표시 순서대로 전부 조회한다. 노출 설정을 한 적이 "
            + "있는 후보가 그 순서대로 먼저 오고, 아직 설정이 없는 후보가 신청 마감 임박순으로 뒤에 붙으며 "
            + "노출 여부는 false로 내려간다. 그래서 isExposed=true로 거르면 설정이 없는 후보는 모두 빠진다 — "
            + "순서 변경은 거르지 않은 목록으로 보내야 한다. 이 조회는 아무것도 저장하지 않는다. "
            + "순서를 드래그로 바꾸는 목록이라 페이지로 자르지 않는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    ApiResponse<AdminHomeApplySectionListResponse> getApplySections(
        AdminApiUser apiUser,
        @ParameterObject AdminHomeApplySectionSearchParams params
    );

    @Operation(summary = "신청 섹션 노출 여부 변경",
        description = "후보 하나를 홈에 띄울지 바꾼다. 설정이 아직 없던 후보면 이때 만들며, 표시 순서는 맨 뒤가 된다. "
            + "대상은 경로가 아니라 본문으로 받는다. 노출로 켜도 그 행사·회차의 신청이 끝나면 홈에서 빠진다. "
            + "설정이 아직 없던 같은 후보를 두 운영진이 동시에 바꾸면 늦은 쪽이 409다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = HomeErrorCode.class, codes = {"HOME_APPLY_SECTION_CONFLICT"})
    ApiResponse<Void> updateExposure(AdminApiUser apiUser, AdminHomeApplySectionExposureRequest request);

    @Operation(summary = "신청 섹션 순서 변경",
        description = "보낸 대상에 앞에서부터 표시 순서를 다시 매긴다. 설정이 아직 없던 후보는 이때 미노출로 만든다. "
            + "후보 전체와 일치하는지는 보지 않는다 — 후보는 신청 기간이 지나거나 선착순 정원이 차면 저절로 "
            + "바뀌므로, 목록을 띄워둔 사이 달라졌다고 요청을 거부하면 영문 모를 실패가 된다. 그사이 신청을 "
            + "새로 연 행사는 목록 뒤에 설정 없이 남고, 마감된 대상은 순서만 받고 후보에서 빠진다. 다만 노출 "
            + "여부로 거른 목록으로는 보내지 않는다 — 걸러진 설정의 옛 순서값이 남아 새로 매긴 값과 겹친다. "
            + "두 운영진이 동시에 바꾸면 나중에 저장한 순서가 남지만, 설정이 아직 없던 대상을 둘이 함께 "
            + "보냈으면 늦은 쪽이 409다 — 다시 불러와 보내면 된다. 같은 대상을 두 번 보내면 400이다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = HomeErrorCode.class,
        codes = {"HOME_APPLY_SECTION_DUPLICATE_TARGET", "HOME_APPLY_SECTION_CONFLICT"})
    ApiResponse<Void> updateOrders(AdminApiUser apiUser, AdminHomeApplySectionOrderUpdateRequest request);
}
