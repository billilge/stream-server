package kr.ac.kookmin.stream.api.admin.event.archive.usecase;

import java.util.List;
import java.util.Set;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveWriteCommand;
import kr.ac.kookmin.stream.event.domain.archive.service.AdminArchiveService;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;
import kr.ac.kookmin.stream.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 아카이빙을 쓰기 전에 연결할 사진과 행사가 실제로 있는지 확인한다. 사진(file)과 행사는 아카이빙과 다른 도메인이라
 * 도메인끼리 직접 의존하지 않고 이 UseCase가 조합한다({@code architecture.md} 6-1절).
 * <p>
 * 확인과 저장을 한 트랜잭션으로 묶지 않는다. 확인 직후 사진이 지워져도 상세 조회가 목록에서 조용히 빼고, 행사가
 * 지워지면 연결만 비어 보인다 — 둘 다 아카이빙을 못 쓰게 만들지 않는다. 공지와 같은 판단이다
 * ({@code AdminNoticeWriteUseCase}).
 */
@Component
@RequiredArgsConstructor
public class AdminArchiveWriteUseCase {

    private final FileService fileService;
    private final EventService eventService;
    private final AdminArchiveService adminArchiveService;

    public void create(ArchiveWriteCommand command) {
        validateImagesExist(command.imageIds());
        validateRelatedEventExists(command.relatedEventId());

        adminArchiveService.create(command);
    }

    public void update(Long archiveId, ArchiveWriteCommand command) {
        validateImagesExist(command.imageIds());
        validateRelatedEventExists(command.relatedEventId());

        adminArchiveService.update(archiveId, command);
    }

    /**
     * 조회된 개수를 견줘 전부 있는지 본다. 중복이 남아 있으면 있는 파일도 없다고 오판하므로 호출부에 맡기지 않고
     * 여기서 묶는다.
     */
    private void validateImagesExist(List<Long> imageIds) {
        Set<Long> fileIds = Set.copyOf(imageIds);

        if (fileService.findAllByIdIn(List.copyOf(fileIds)).size() != fileIds.size()) {
            throw new BusinessException(FileErrorCode.FILE_NOT_FOUND);
        }
    }

    /**
     * 없는 행사를 연결하면 상세에서 조용히 비어 보여 운영진이 눈치채지 못한다. 쓰는 자리에서 막는다.
     * 아직 게시하지 않은 행사는 연결할 수 있다 — 운영진 화면은 미게시 행사도 고를 수 있어야 한다.
     */
    private void validateRelatedEventExists(Long relatedEventId) {
        if (relatedEventId != null && eventService.getByIdOrNull(relatedEventId) == null) {
            throw new BusinessException(EventErrorCode.EVENT_NOT_FOUND);
        }
    }
}
