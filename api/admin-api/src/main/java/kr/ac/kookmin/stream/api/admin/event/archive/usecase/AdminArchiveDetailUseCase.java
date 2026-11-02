package kr.ac.kookmin.stream.api.admin.event.archive.usecase;

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
 * 상세 조회 자체는 학생 앱과 내용이 같아 {@link ArchiveService}를 그대로 쓴다({@code coding-style.md} 2-7절).
 * 학생 앱 쪽({@code ArchiveDetailUseCase})과 달리 <b>미게시 행사도 보여준다</b> — 운영진은 아직 공개하지 않은
 * 행사도 연결 대상으로 확인할 수 있어야 한다.
 */
@Component
@RequiredArgsConstructor
public class AdminArchiveDetailUseCase {

    private final ArchiveService archiveService;
    private final EventService eventService;

    public ArchiveWithRelatedEvent getArchive(Long archiveId) {
        ArchiveDetail detail = archiveService.getArchive(archiveId);
        return new ArchiveWithRelatedEvent(detail, resolveTitle(detail.relatedEventId()));
    }

    /**
     * 연결한 행사가 지워졌으면 {@code null}을 돌려준다. 아카이빙까지 404로 만들지 않고 연결만 빼서 보여준다.
     */
    private String resolveTitle(Long relatedEventId) {
        if (relatedEventId == null) {
            return null;
        }

        Event event = eventService.getByIdOrNull(relatedEventId);
        return event == null ? null : event.getTitle();
    }
}
