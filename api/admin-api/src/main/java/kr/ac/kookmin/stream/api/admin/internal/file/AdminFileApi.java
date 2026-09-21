package kr.ac.kookmin.stream.api.admin.internal.file;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.internal.file.request.FileUploadUrlIssueRequest;
import kr.ac.kookmin.stream.api.admin.internal.file.response.FileUploadUrlIssueResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.internal.domain.file.domain.FileErrorCode;

/**
 * 운영진 파일 API의 문서 명세. 구현은 {@link AdminFileController}가 맡는다.
 * <p>
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다. 경로 매핑과
 * 파라미터 바인딩(@{@code RequestBody}, @{@code PathVariable} 등)은 구현체에 둔다.
 */
@Tag(name = "파일", description = "운영진 파일 업로드 URL 발급·삭제")
public interface AdminFileApi {

    /** 업로드용 presigned URL 발급. 파일 형식·크기 정책을 통과하면 업로드 URL을 내려준다. */
    @Operation(summary = "파일 업로드 URL 발급",
        description = "파일 형식·크기 정책을 검증한 뒤 업로드용 presigned URL을 발급한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = FileErrorCode.class, codes = {"UNSUPPORTED_FILE_EXTENSION", "FILE_SIZE_EXCEEDED"})
    ApiResponse<FileUploadUrlIssueResponse> issuePresignedUrl(FileUploadUrlIssueRequest request);

    /** 파일 삭제. */
    @Operation(summary = "파일 삭제")
    @ApiErrorCode(type = FileErrorCode.class, codes = {"FILE_NOT_FOUND"})
    ApiResponse<Void> delete(Long fileId);
}
