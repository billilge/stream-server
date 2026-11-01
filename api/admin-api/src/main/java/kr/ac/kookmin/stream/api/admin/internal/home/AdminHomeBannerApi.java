package kr.ac.kookmin.stream.api.admin.internal.home;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.internal.home.request.AdminHomeBannerOrderUpdateRequest;
import kr.ac.kookmin.stream.api.admin.internal.home.request.AdminHomeBannerSearchParams;
import kr.ac.kookmin.stream.api.admin.internal.home.request.AdminHomeBannerTypeParams;
import kr.ac.kookmin.stream.api.admin.internal.home.request.AdminHomeBannerWriteRequest;
import kr.ac.kookmin.stream.api.admin.internal.home.response.AdminHomeBannerDetailResponse;
import kr.ac.kookmin.stream.api.admin.internal.home.response.AdminHomeBannerListResponse;
import kr.ac.kookmin.stream.api.admin.internal.home.response.AdminHomeBannerSummaryResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 홈 배너 API의 문서 명세. 구현은 {@link AdminHomeBannerController}가 맡는다.
 * <p>
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다.
 */
@Tag(name = "홈 배너 (운영진)", description = "홈 화면 배너 조회·등록·수정·삭제·순서 변경")
public interface AdminHomeBannerApi {

    @Operation(summary = "노출 중 배너 목록 조회",
        description = "노출이 끝나지 않은 배너를 표시 순서대로 전부 조회한다. 노출 중과 노출 예정이 함께 담긴다. "
            + "순서를 드래그로 바꾸는 목록이라 페이지로 자르지 않는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    ApiResponse<AdminHomeBannerListResponse> getCurrentBanners(
        AdminApiUser apiUser,
        @ParameterObject AdminHomeBannerSearchParams params
    );

    @Operation(summary = "지난 배너 목록 조회",
        description = "노출이 끝난 배너를 종료 일시 최신순으로 페이지 조회한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    ApiResponse<PageResponse<AdminHomeBannerSummaryResponse>> getPastBanners(
        AdminApiUser apiUser,
        @ParameterObject AdminHomeBannerTypeParams params,
        @ParameterObject PageParams pageParams
    );

    @Operation(summary = "배너 상세 조회",
        description = "배너 한 건을 조회한다. 수정 화면의 초기값으로도 쓴다. "
            + "연결된 공지·행사가 지워졌으면 연결 대상만 비워 내려준다. 아직 공개하지 않은 행사는 그대로 내려준다.")
    @ApiErrorCode(type = HomeErrorCode.class, codes = {"HOME_BANNER_NOT_FOUND"})
    ApiResponse<AdminHomeBannerDetailResponse> getBanner(AdminApiUser apiUser, Long bannerId);

    /** 배너 등록. 표시 순서는 서버가 맨 뒤로 정한다. */
    @Operation(summary = "배너 등록",
        description = "배너를 등록하고 노출 순서의 맨 뒤에 붙인다. 이미지는 업로드를 마친 파일 ID로 넘기며 "
            + "프리셋도 같은 방식이다. 랜딩이 NOTICE·EVENT면 targetId가, EXTERNAL_LINK면 linkUrl이 있어야 하고 "
            + "연결 대상이 실제로 있는지 확인한다. 아직 공개하지 않은 행사도 연결할 수 있다. "
            + "응답 본문은 없다 — 등록을 마치면 목록으로 돌아간다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = HomeErrorCode.class,
        codes = {"HOME_BANNER_INVALID_LANDING", "HOME_BANNER_INVALID_EXPOSURE_PERIOD"})
    @ApiErrorCode(type = FileErrorCode.class, codes = {"FILE_NOT_FOUND"})
    @ApiErrorCode(type = NoticeErrorCode.class, codes = {"NOTICE_NOT_FOUND"})
    @ApiErrorCode(type = EventErrorCode.class, codes = {"EVENT_NOT_FOUND"})
    ApiResponse<Void> createBanner(AdminApiUser apiUser, AdminHomeBannerWriteRequest request);

    /** 배너 수정. 두 운영진이 겹쳐 수정하면 나중에 저장한 내용이 반영된다. */
    @Operation(summary = "배너 수정",
        description = "배너의 입력값을 통째로 바꾼다. 표시 순서는 그대로 두며, 순서는 노출 순서 변경으로 바꾼다. "
            + "수정 화면은 배너 상세 조회로 채운다. 다른 운영진이 먼저 고쳤는지는 확인하지 않으므로 나중에 저장한 "
            + "내용이 그대로 반영된다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = HomeErrorCode.class,
        codes = {"HOME_BANNER_NOT_FOUND", "HOME_BANNER_INVALID_LANDING", "HOME_BANNER_INVALID_EXPOSURE_PERIOD"})
    @ApiErrorCode(type = FileErrorCode.class, codes = {"FILE_NOT_FOUND"})
    @ApiErrorCode(type = NoticeErrorCode.class, codes = {"NOTICE_NOT_FOUND"})
    @ApiErrorCode(type = EventErrorCode.class, codes = {"EVENT_NOT_FOUND"})
    ApiResponse<Void> updateBanner(AdminApiUser apiUser, Long bannerId, AdminHomeBannerWriteRequest request);

    /** 배너 삭제. 소프트 삭제라 학생 앱 홈과 운영진 목록에서 보이지 않게 된다. */
    @Operation(summary = "배너 삭제",
        description = "배너를 삭제한다. 데이터는 남기고 목록과 홈에서만 보이지 않게 한다. 연결된 파일은 지우지 않는다. "
            + "남은 배너의 표시 순서는 다시 매기지 않는다 — 사이가 비어도 순서는 그대로다.")
    @ApiErrorCode(type = HomeErrorCode.class, codes = {"HOME_BANNER_NOT_FOUND"})
    ApiResponse<Void> deleteBanner(AdminApiUser apiUser, Long bannerId);

    /** 노출 순서 일괄 변경. 노출이 끝나지 않은 배너 전체를 바뀐 순서대로 받는다. */
    @Operation(summary = "배너 노출 순서 변경",
        description = "노출이 끝나지 않은 배너(노출 중·노출 예정)의 표시 순서를 받은 순서대로 다시 매긴다. "
            + "그 배너 전체를 보내야 하며, 분류나 상태 필터를 켠 일부 목록으로는 바꿀 수 없다. "
            + "보낸 목록이 서버의 목록과 다르면 아무것도 바꾸지 않고 409를 돌려준다. 그사이 다른 운영진이 배너를 "
            + "등록·삭제한 경우뿐 아니라, 목록을 띄워둔 사이 배너 하나의 노출 기간이 끝나도 어긋난다 — 서버는 "
            + "요청을 받은 시각으로 다시 읽기 때문이다. 노출이 끝난 배너의 순서는 손대지 않는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = HomeErrorCode.class, codes = {"HOME_BANNER_ORDER_MISMATCH"})
    ApiResponse<Void> updateBannerOrders(AdminApiUser apiUser, AdminHomeBannerOrderUpdateRequest request);
}
