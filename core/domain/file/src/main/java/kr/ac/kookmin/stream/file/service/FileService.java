package kr.ac.kookmin.stream.file.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.file.domain.FileUploadUrlIssueCommand;
import kr.ac.kookmin.stream.file.domain.FileUploadUrlIssueResult;

public interface FileService {
    FileUploadUrlIssueResult issuePresignedUrl(FileUploadUrlIssueCommand command);

    /** 올린 본인만 지울 수 있다. 관리자도 예외 없이, 남의 파일이면 FILE_DELETE_FORBIDDEN. */
    void delete(Long memberId, Long fileId);

    /**
     * 가리키던 행이 사라져 더는 쓰이지 않는 파일을 서버가 정리할 때만 쓴다. 사용자 요청 삭제가 아니라 올린 사람을 확인하지 않는다.
     * 없으면 FILE_NOT_FOUND.
     */
    void deleteUnreferenced(Long fileId);
    Optional<File> findById(Long fileId);

    /** 업로드 URL 발급 때 돌려준 키로 찾는다. 제출받은 키가 실재하는지, 누가 올렸는지 확인할 때 쓴다. */
    Optional<File> findByFileKey(String fileKey);
    Map<Long, File> findAllByIdIn(List<Long> fileIds);

    /** fileId로 저장된 파일 key를 꺼낸다. 없으면 FILE_NOT_FOUND. */
    String resolveKey(Long fileId);
}
