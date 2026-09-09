package kr.ac.kookmin.stream.internal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import kr.ac.kookmin.stream.ApiResponse;
import kr.ac.kookmin.stream.common.PrincipalProvider;
import kr.ac.kookmin.stream.internal.domain.file.domain.FileUploadUrlIssueResult;
import kr.ac.kookmin.stream.internal.domain.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/files")
@RequiredArgsConstructor
public class AdminFileController {

    private static final String LOCAL_UPLOAD_PATH = "/local-upload/";

    private final FileService fileService;
    private final PrincipalProvider principalProvider;

    @PostMapping("/presigned-url")
    public ApiResponse<FileUploadUrlIssueResponse> issueUploadUrl(@Valid @RequestBody FileUploadUrlIssueRequest request) {
        FileUploadUrlIssueResult result = fileService.issueUploadUrl(request.toCommand(principalProvider.userId()));
        return ApiResponse.success(FileUploadUrlIssueResponse.from(result));
    }

    @DeleteMapping("/{fileId}")
    public ApiResponse<Void> delete(@PathVariable Long fileId) {
        fileService.delete(fileId);
        return ApiResponse.success();
    }

    /**
     * S3 연동 전 임시 엔드포인트. presigned-url 발급 응답의 uploadUrl이 이 경로를 가리킨다.
     * S3로 전환하면 이 메서드와 FileClientImpl의 로컬 구현을 함께 제거한다.
     */
    @PutMapping("/local-upload/**")
    public ApiResponse<Void> receiveLocalUpload(HttpServletRequest request) throws IOException {
        String fileKey = extractFileKey(request);
        fileService.receiveUpload(fileKey, request.getInputStream());
        return ApiResponse.success();
    }

    private String extractFileKey(HttpServletRequest request) {
        String uri = request.getRequestURI();
        int index = uri.indexOf(LOCAL_UPLOAD_PATH);
        return uri.substring(index + LOCAL_UPLOAD_PATH.length());
    }
}
