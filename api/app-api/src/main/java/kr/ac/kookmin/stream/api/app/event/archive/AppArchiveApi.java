package kr.ac.kookmin.stream.api.app.event.archive;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.app.event.archive.request.ArchiveListParams;
import kr.ac.kookmin.stream.api.app.event.archive.response.ArchiveDetailResponse;
import kr.ac.kookmin.stream.api.app.event.archive.response.ArchiveListResponse;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 학생 앱 행사 아카이브 API의 문서 명세. 구현은 {@link AppArchiveController}가 맡는다.
 * <p>
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다. 경로 매핑과
 * 파라미터 바인딩(@{@code ModelAttribute}, @{@code PathVariable} 등)은 구현체에 둔다.
 */
@Tag(name = "행사 아카이브", description = "학생 앱 지난 행사 아카이브 조회")
public interface AppArchiveApi {

    /** 아카이브 목록과 연도 목록. 연도를 지정하면 해당 연도만 필터링한다. */
    @Operation(summary = "행사 아카이브 목록 조회",
        description = "지난 행사 아카이브 목록과 필터용 연도 목록을 함께 조회한다. year를 지정하면 해당 연도만 필터링한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    ApiResponse<ArchiveListResponse> getArchives(@ParameterObject ArchiveListParams params);

    /** 아카이브 상세. 연결된 행사가 있으면 눌러서 이동할 수 있게 함께 내려준다. */
    @Operation(summary = "행사 아카이브 상세 조회",
        description = "아카이브 한 건을 조회한다. 현장 사진은 저장된 순서가 곧 표시 순서이고, 삭제된 파일은 "
            + "조용히 빠진다. relatedEvent는 연결한 행사이고 제목은 조회할 때마다 원본에서 가져와 항상 최신이다. "
            + "연결하지 않았거나 행사가 삭제·미게시면 null이다.")
    @ApiErrorCode(type = ArchiveErrorCode.class, codes = {"ARCHIVE_NOT_FOUND"})
    ApiResponse<ArchiveDetailResponse> getArchive(Long archiveId);
}
