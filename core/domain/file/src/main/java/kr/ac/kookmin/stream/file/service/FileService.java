package kr.ac.kookmin.stream.file.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.file.domain.FileUploadUrlIssueCommand;
import kr.ac.kookmin.stream.file.domain.FileUploadUrlIssueResult;

public interface FileService {
    FileUploadUrlIssueResult issuePresignedUrl(FileUploadUrlIssueCommand command);
    void delete(Long fileId);
    Optional<File> findById(Long fileId);
    Map<Long, File> findAllByIdIn(List<Long> fileIds);

    /** fileId로 저장된 파일 key를 꺼낸다. 없으면 FILE_NOT_FOUND. */
    String resolveKey(Long fileId);
}
