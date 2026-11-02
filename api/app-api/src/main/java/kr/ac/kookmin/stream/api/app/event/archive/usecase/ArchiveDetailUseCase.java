package kr.ac.kookmin.stream.api.app.event.archive.usecase;

import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveDetail;
import kr.ac.kookmin.stream.event.domain.archive.service.ArchiveService;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 아카이빙 상세에 연결한 행사의 제목을 붙인다. 아카이빙과 행사는 같은 모듈의 서로 다른 도메인이라 서비스끼리
 * 부르지 않고 이 UseCase가 조합한다({@code architecture.md} 6-1절, 레이어 규칙 4).
 * <p>
 * 제목을 저장하지 않고 조회할 때마다 가져오므로 행사명을 고쳐도 아카이빙 화면에 최신 제목이 뜬다.
 */
@Component
@RequiredArgsConstructor
public class ArchiveDetailUseCase {

    private final ArchiveService archiveService;
    private final EventService eventService;

    public ArchiveWithRelatedEvent getArchive(Long archiveId) {
        ArchiveDetail detail = archiveService.getArchive(archiveId);
        return new ArchiveWithRelatedEvent(detail, resolveTitle(detail.relatedEventId()));
    }

    /**
     * 연결한 행사가 지워졌으면 {@code null}을 돌려준다. 아카이빙까지 404로 만들지 않고 연결만 빼서 보여준다.
     * 아직 게시하지 않은 행사도 학생에게는 없는 것으로 본다.
     */
    private String resolveTitle(Long relatedEventId) {
        if (relatedEventId == null) {
            return null;
        }

        Event event = eventService.getByIdOrNull(relatedEventId);
        return event == null || !event.isPublished() ? null : event.getTitle();
    }
}
