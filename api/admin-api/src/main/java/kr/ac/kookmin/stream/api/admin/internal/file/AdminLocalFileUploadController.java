package kr.ac.kookmin.stream.api.admin.internal.file;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.internal.domain.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * S3 연동 전 임시 컨트롤러. presigned-url 발급 응답의 uploadUrl이 이 경로를 가리킨다.
 * S3로 전환하면 이 클래스와 LocalFileStorageClient의 로컬 구현을 함께 제거한다.
 */
@RestController
@RequestMapping("/v1/admin/files")
@RequiredArgsConstructor
public class AdminLocalFileUploadController implements AdminLocalFileUploadApi {

    private static final String LOCAL_UPLOAD_PATH = "/local-upload/";

    private final FileService fileService;

    @Override
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
