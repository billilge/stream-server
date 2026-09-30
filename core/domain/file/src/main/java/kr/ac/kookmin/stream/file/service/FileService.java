package kr.ac.kookmin.stream.file.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kr.ac.kookmin.stream.file.domain.FileInfo;
import kr.ac.kookmin.stream.file.domain.FileUploadUrlIssueCommand;
import kr.ac.kookmin.stream.file.domain.FileUploadUrlIssueResult;
import kr.ac.kookmin.stream.file.domain.FileUrl;

public interface FileService {
    FileUploadUrlIssueResult issuePresignedUrl(FileUploadUrlIssueCommand command);
    void receiveUpload(String fileKey, InputStream content);
    void delete(Long fileId);
    Optional<FileUrl> resolveUrl(Long fileId);
    Map<Long, FileUrl> resolveUrls(List<Long> fileIds);
    Optional<FileInfo> resolveInfo(Long fileId);
    Map<Long, FileInfo> resolveInfos(List<Long> fileIds);
    FileUrl publicUrl(String fileKey);
    Map<String, FileUrl> publicUrls(List<String> fileKeys);
}
