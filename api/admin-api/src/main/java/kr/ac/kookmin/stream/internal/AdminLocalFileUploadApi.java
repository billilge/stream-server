package kr.ac.kookmin.stream.internal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import kr.ac.kookmin.stream.ApiErrorCode;
import kr.ac.kookmin.stream.ApiResponse;
import kr.ac.kookmin.stream.internal.domain.file.domain.FileErrorCode;

/**
 * 로컬 업로드 수신 API의 문서 명세. 구현은 {@link AdminLocalFileUploadController}가 맡는다.
 * <p>
 * S3 연동 전 임시 엔드포인트다. presigned-url 발급 응답의 uploadUrl이 이 경로를 가리킨다.
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다.
 */
@Tag(name = "파일 로컬 업로드", description = "S3 전환 전 임시 로컬 업로드 수신 엔드포인트")
public interface AdminLocalFileUploadApi {

    /** presigned URL로 전달받은 파일 바이트를 로컬에 저장한다. */
    @Operation(summary = "로컬 업로드 수신",
        description = "발급된 로컬 업로드 URL로 들어온 파일 바이트를 저장한다. S3 전환 시 제거된다.")
    @ApiErrorCode(type = FileErrorCode.class, codes = {"FILE_NOT_FOUND"})
    ApiResponse<Void> receiveLocalUpload(HttpServletRequest request) throws IOException;
}
