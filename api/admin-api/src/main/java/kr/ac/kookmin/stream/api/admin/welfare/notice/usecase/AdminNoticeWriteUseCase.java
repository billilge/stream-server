package kr.ac.kookmin.stream.api.admin.welfare.notice.usecase;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;
import kr.ac.kookmin.stream.file.service.FileService;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCreateCommand;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeUpdateCommand;
import kr.ac.kookmin.stream.welfare.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 공지를 쓰기 전에 연결할 이미지·첨부 파일이 실제로 있는지 확인한다. 공지(welfare)와 파일(file)은 서로 다른 도메인이라
 * 도메인끼리 직접 의존하지 않고 이 UseCase가 두 Service를 조합한다.
 * <p>
 * 파일 확인과 공지 저장은 한 트랜잭션으로 묶지 않는다. 확인 직후 파일이 지워져도 공지 조회가 삭제된 파일을
 * 목록에서 조용히 제외하므로 원자성이 필요하지 않다.
 */
@Component
@RequiredArgsConstructor
public class AdminNoticeWriteUseCase {

    private final NoticeService noticeService;
    private final FileService fileService;

    public Notice create(NoticeCreateCommand command) {
        requireFilesExist(command.imageIds(), command.attachmentIds());
        return noticeService.create(command);
    }

    public Notice update(Long noticeId, NoticeUpdateCommand command) {
        requireFilesExist(command.imageIds(), command.attachmentIds());
        return noticeService.update(noticeId, command);
    }

    private void requireFilesExist(List<Long> imageIds, List<Long> attachmentIds) {
        Set<Long> fileIds = new LinkedHashSet<>(imageIds);
        fileIds.addAll(attachmentIds);

        if (fileService.findAllByIdIn(List.copyOf(fileIds)).size() != fileIds.size()) {
            throw new BusinessException(FileErrorCode.FILE_NOT_FOUND);
        }
    }
}
