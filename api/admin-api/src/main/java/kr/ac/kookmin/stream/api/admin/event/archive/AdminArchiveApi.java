package kr.ac.kookmin.stream.api.admin.event.archive;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.event.archive.request.ArchiveSearchParams;
import kr.ac.kookmin.stream.api.admin.event.archive.response.AdminArchiveDetailResponse;
import kr.ac.kookmin.stream.api.admin.event.archive.response.AdminArchiveListItemResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 아카이빙 API의 문서 명세. 구현은 {@link AdminArchiveController}가 맡는다.
 * <p>
 * 접근은 {@code ADMIN} role이면 된다. 부서 제한은 두지 않았다.
 */
@Tag(name = "아카이빙 관리", description = "운영진 아카이빙 목록·상세 조회")
public interface AdminArchiveApi {

    /** 아카이빙 목록. 제목으로 검색하고 슬랑제 노출 여부로 거르며, 등록일 최신순으로 페이지 단위 조회한다. */
    @Operation(summary = "아카이빙 목록 조회",
        description = "삭제되지 않은 아카이빙을 등록일 최신순으로 페이지 단위로 조회한다. "
            + "keyword를 주면 제목에 그 글자가 들어 있는 것만, isExposed를 주면 슬랑제 페이지 노출 여부가 "
            + "그 값인 것만 조회하며 둘을 함께 쓸 수 있다. 생략하면 좁히지 않는다. page는 0부터 시작한다. "
            + "학생 앱 목록이 활동 시작일 내림차순인 것과 달리 등록일 기준이다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    ApiResponse<PageResponse<AdminArchiveListItemResponse>> getArchives(
        AdminApiUser apiUser,
        @ParameterObject ArchiveSearchParams searchParams,
        @ParameterObject PageParams pageParams
    );

    /** 아카이빙 상세. 게시물 상세 화면과 수정 화면의 기존 값 채우기에 함께 쓴다. */
    @Operation(summary = "아카이빙 상세 조회",
        description = "아카이빙 한 건을 조회한다. 당일 활동은 endDate가 null이다. "
            + "활동 사진은 노출 순서대로 담고 첫 장이 대표 이미지다 — 대표를 따로 내려주지 않는다. "
            + "삭제된 파일은 사진 목록에서 조용히 빠진다.")
    @ApiErrorCode(type = ArchiveErrorCode.class, codes = {"ARCHIVE_NOT_FOUND"})
    ApiResponse<AdminArchiveDetailResponse> getArchive(AdminApiUser apiUser, Long archiveId);
}
