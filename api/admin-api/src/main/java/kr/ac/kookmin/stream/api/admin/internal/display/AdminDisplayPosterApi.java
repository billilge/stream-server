package kr.ac.kookmin.stream.api.admin.internal.display;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.internal.display.request.DisplayPosterActiveRequest;
import kr.ac.kookmin.stream.api.admin.internal.display.request.DisplayPosterCreateRequest;
import kr.ac.kookmin.stream.api.admin.internal.display.request.DisplayPosterUpdateRequest;
import kr.ac.kookmin.stream.api.admin.internal.display.response.DisplayPosterActiveResponse;
import kr.ac.kookmin.stream.api.admin.internal.display.response.DisplayPosterCreateResponse;
import kr.ac.kookmin.stream.api.admin.internal.display.response.DisplayPosterListResponse;
import kr.ac.kookmin.stream.api.admin.internal.display.response.DisplayPosterUpdateResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayErrorCode;

/**
 * 운영진 디스플레이 포스터 API의 문서 명세. 구현은 {@link AdminDisplayPosterController}가 맡는다.
 * <p>
 * 접근은 {@code ADMIN} role이면 된다. 부서 제한은 두지 않았다.
 * <p>
 * 이미지는 파일 업로드 URL 발급({@code POST /v1/files/presigned-url}, category {@code DISPLAY_POSTER})으로 먼저 올리고
 * 받은 fileId를 넘긴다.
 */
@Tag(name = "디스플레이 포스터 관리", description = "운영진 디스플레이 행사 포스터 조회·등록·수정·삭제·활성화 여부 변경")
public interface AdminDisplayPosterApi {

    /** 포스터 목록. 최신 등록 순 전체. */
    @Operation(summary = "포스터 목록 조회",
        description = "포스터 전체를 최신 등록 순으로 조회한다. 페이지로 나누지 않는다. 활성화 개수는 active가 true인 항목을 "
            + "세면 된다. 이미지 파일이 지워진 포스터는 imageUrl이 null이다.")
    ApiResponse<DisplayPosterListResponse> getPosters(AdminApiUser apiUser);

    /** 포스터 등록. 등록하면 바로 활성 상태가 되어 디스플레이에 노출된다. */
    @Operation(summary = "포스터 등록",
        description = "포스터를 등록한다. 이미지는 업로드를 마친 파일 ID로 넘긴다. 없는 파일이거나 포스터용(DISPLAY_POSTER)으로 "
            + "올린 파일이 아니면 FILE_NOT_FOUND로 등록하지 않는다. 등록한 포스터는 바로 활성 상태다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = FileErrorCode.class, codes = {"FILE_NOT_FOUND"})
    ApiResponse<DisplayPosterCreateResponse> createPoster(AdminApiUser apiUser, DisplayPosterCreateRequest request);

    /** 포스터 수정. 포스터명·이미지를 통째로 바꾸고, 이미지가 바뀌면 이전 이미지 파일을 지운다. */
    @Operation(summary = "포스터 수정",
        description = "포스터명과 이미지를 통째로 바꾼다. 이미지를 그대로 두려면 목록 조회에서 받은 imageFileId를 다시 보낸다. "
            + "없는 파일이거나 포스터용(DISPLAY_POSTER)으로 올린 파일이 아니면 FILE_NOT_FOUND다. 이미지가 바뀌면 이전 이미지 "
            + "파일을 지운다. 활성화 여부는 바뀌지 않는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = FileErrorCode.class, codes = {"FILE_NOT_FOUND"})
    @ApiErrorCode(type = DisplayErrorCode.class, codes = {"DISPLAY_POSTER_NOT_FOUND"})
    ApiResponse<DisplayPosterUpdateResponse> updatePoster(
        AdminApiUser apiUser, Long posterId, DisplayPosterUpdateRequest request);

    /** 포스터 삭제. 물리 삭제이며 이미지 파일도 함께 지운다. */
    @Operation(summary = "포스터 삭제",
        description = "포스터를 DB에서 지우고 이미지 파일도 함께 지운다. 이미지 파일 삭제가 실패해도 포스터는 지워진다.")
    @ApiErrorCode(type = DisplayErrorCode.class, codes = {"DISPLAY_POSTER_NOT_FOUND"})
    ApiResponse<Void> deletePoster(AdminApiUser apiUser, Long posterId);

    /** 포스터 활성화 여부 변경. 비활성화한 포스터는 디스플레이에 노출되지 않는다. */
    @Operation(summary = "포스터 활성화 여부 변경",
        description = "포스터의 활성화 여부를 요청 값으로 바꾼다. 이미 같은 상태여도 성공한다. 비활성화한 포스터는 디스플레이 화면 "
            + "조회에서 빠진다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = DisplayErrorCode.class, codes = {"DISPLAY_POSTER_NOT_FOUND"})
    ApiResponse<DisplayPosterActiveResponse> changeActive(
        AdminApiUser apiUser, Long posterId, DisplayPosterActiveRequest request);
}
