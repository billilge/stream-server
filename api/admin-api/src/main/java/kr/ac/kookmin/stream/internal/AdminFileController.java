package kr.ac.kookmin.stream.internal;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.ApiResponse;
import kr.ac.kookmin.stream.common.PrincipalProvider;
import kr.ac.kookmin.stream.internal.domain.file.domain.FileUploadUrlIssueResult;
import kr.ac.kookmin.stream.internal.domain.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/files")
@RequiredArgsConstructor
public class AdminFileController {

    private final FileService fileService;
    private final PrincipalProvider principalProvider;

    @PostMapping("/presigned-url")
    public ApiResponse<FileUploadUrlIssueResponse> issuePresignedUrl(@Valid @RequestBody FileUploadUrlIssueRequest request) {
        FileUploadUrlIssueResult result = fileService.issuePresignedUrl(request.toCommand(principalProvider.userId()));
        return ApiResponse.success(FileUploadUrlIssueResponse.from(result));
    }

    @DeleteMapping("/{fileId}")
    public ApiResponse<Void> delete(@PathVariable Long fileId) {
        fileService.delete(fileId);
        return ApiResponse.success();
    }
}
