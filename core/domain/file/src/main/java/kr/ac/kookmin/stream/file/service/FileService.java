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

    /** 업로드 URL 발급 때 돌려준 키로 찾는다. 제출받은 키가 실재하는지, 누가 올렸는지 확인할 때 쓴다. */
    Optional<File> findByFileKey(String fileKey);
    Map<Long, File> findAllByIdIn(List<Long> fileIds);
}
