package kr.ac.kookmin.stream.internal.domain.file.service;

import java.io.InputStream;
import kr.ac.kookmin.stream.internal.domain.file.domain.FileUploadUrlIssueCommand;
import kr.ac.kookmin.stream.internal.domain.file.domain.FileUploadUrlIssueResult;

public interface FileService {
    FileUploadUrlIssueResult issueUploadUrl(FileUploadUrlIssueCommand command);
    void receiveUpload(String fileKey, InputStream content);
    void delete(Long fileId);
}
