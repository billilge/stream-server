package kr.ac.kookmin.stream.api.admin.event.archive;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.event.archive.request.ArchiveExposureChangeRequest;
import kr.ac.kookmin.stream.api.admin.event.archive.request.ArchiveSearchParams;
import kr.ac.kookmin.stream.api.admin.event.archive.request.ArchiveWriteRequest;
import kr.ac.kookmin.stream.api.admin.event.archive.response.AdminArchiveDetailResponse;
import kr.ac.kookmin.stream.api.admin.event.archive.response.AdminArchiveListItemResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 아카이빙 API의 문서 명세. 구현은 {@link AdminArchiveController}가 맡는다.
 * <p>
 * 접근은 {@code ADMIN} role이면 된다. 부서 제한은 두지 않았다.
 */
@Tag(name = "아카이빙 관리", description = "운영진 아카이빙 목록·상세 조회와 등록·수정·삭제")
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
            + "삭제된 파일은 사진 목록에서 조용히 빠진다. "
            + "relatedEvent는 연결한 행사이고, 제목은 저장하지 않고 조회할 때마다 원본에서 가져온다. "
            + "연결하지 않았거나 행사가 삭제됐으면 null이다 — 운영진 화면이므로 아직 게시하지 않은 행사도 보여준다.")
    @ApiErrorCode(type = ArchiveErrorCode.class, codes = {"ARCHIVE_NOT_FOUND"})
    ApiResponse<AdminArchiveDetailResponse> getArchive(AdminApiUser apiUser, Long archiveId);

    @Operation(summary = "아카이빙 등록",
        description = "아카이빙을 새로 등록한다. 활동 사진은 한 장 이상이어야 하고 첫 장이 대표 이미지다. "
            + "당일 활동은 endDate를 생략한다 — 종료일을 주면 시작일보다 앞설 수 없다. "
            + "relatedEventId로 행사를 연결하며, 아직 게시하지 않은 행사도 연결할 수 있다. "
            + "응답 본문이 없으므로 등록 결과는 목록을 다시 조회해 받는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = ArchiveErrorCode.class, codes = {"INVALID_ARCHIVE_PERIOD"})
    @ApiErrorCode(type = FileErrorCode.class, codes = {"FILE_NOT_FOUND"})
    @ApiErrorCode(type = EventErrorCode.class, codes = {"EVENT_NOT_FOUND"})
    ApiResponse<Void> createArchive(AdminApiUser apiUser, ArchiveWriteRequest request);

    @Operation(summary = "아카이빙 수정",
        description = "아카이빙을 통째로 바꾼다. 보낸 값이 그대로 저장되므로 고치지 않을 필드도 함께 보내야 한다. "
            + "imageIds는 최종 사진 목록이고, relatedEventId를 null로 주면 행사 연결이 끊긴다. "
            + "응답 본문이 없으므로 바뀐 결과는 상세를 다시 조회해 받는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = ArchiveErrorCode.class, codes = {"ARCHIVE_NOT_FOUND", "INVALID_ARCHIVE_PERIOD"})
    @ApiErrorCode(type = FileErrorCode.class, codes = {"FILE_NOT_FOUND"})
    @ApiErrorCode(type = EventErrorCode.class, codes = {"EVENT_NOT_FOUND"})
    ApiResponse<Void> updateArchive(AdminApiUser apiUser, Long archiveId, ArchiveWriteRequest request);

    @Operation(summary = "아카이빙 삭제",
        description = "아카이빙을 삭제한다. 행은 남기고 표시만 바꿔 되살릴 수 있게 하며, 학생 앱 조회에서는 바로 빠진다. "
            + "이미 삭제된 아카이빙은 없는 것으로 본다.")
    @ApiErrorCode(type = ArchiveErrorCode.class, codes = {"ARCHIVE_NOT_FOUND"})
    ApiResponse<Void> deleteArchive(AdminApiUser apiUser, Long archiveId);

    @Operation(summary = "아카이빙 슬랑제 노출 여부 변경",
        description = "여러 아카이빙의 슬랑제 페이지 노출 여부를 한 번에 바꾼다. 목록의 체크박스 일괄 변경과 "
            + "행별 드롭다운 변경에 함께 쓰며, 행별 변경은 archiveIds에 하나만 담아 보낸다. "
            + "한 번에 1개 이상 100개 이하로 보내고, 하나라도 없거나 삭제된 아카이빙이 섞이면 아무것도 바뀌지 않는다. "
            + "응답 본문이 없으므로 바뀐 결과는 목록을 다시 조회해 받는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = ArchiveErrorCode.class, codes = {"ARCHIVE_NOT_FOUND"})
    ApiResponse<Void> changeExposure(AdminApiUser apiUser, ArchiveExposureChangeRequest request);
}
