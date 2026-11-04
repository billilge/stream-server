package kr.ac.kookmin.stream.api.app.search.usecase;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import kr.ac.kookmin.stream.api.app.event.archive.response.ArchiveListItemResponse;
import kr.ac.kookmin.stream.api.app.event.event.response.EventListItemResponse;
import kr.ac.kookmin.stream.api.app.search.response.SearchResponse;
import kr.ac.kookmin.stream.api.app.welfare.feedback.response.FeedbackResponse;
import kr.ac.kookmin.stream.api.app.welfare.notice.response.NoticeListItemResponse;
import kr.ac.kookmin.stream.api.app.welfare.rental.response.ItemListItemResponse;
import kr.ac.kookmin.stream.api.common.dto.PreviewResponse;
import kr.ac.kookmin.stream.common.PreviewResult;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveSummary;
import kr.ac.kookmin.stream.event.domain.archive.service.ArchiveService;
import kr.ac.kookmin.stream.event.domain.event.domain.EventSummary;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.file.service.FileService;
import kr.ac.kookmin.stream.welfare.domain.feedback.service.FeedbackService;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.service.NoticeService;
import kr.ac.kookmin.stream.welfare.domain.rental.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 학생 앱 통합 검색. 행사·아카이빙(event)과 공지·물품·열린피드백(welfare)을 한 번에 찾으므로 서비스끼리 부르지 않고
 * 여기서 조합한다({@code architecture.md} 6-1절). 조회 조합이라 트랜잭션은 걸지 않는다.
 * <p>
 * 섹션마다 목록 화면과 같은 노출 조건·순서로 앞의 몇 건과 전체 수만 담는다. 나머지는 각 목록 API에 같은 검색어를
 * 넘겨 "더보기"로 본다.
 */
@Component
@RequiredArgsConstructor
public class AppSearchUseCase {

    /** 섹션마다 미리보기로 보여줄 건수. */
    private static final int PREVIEW_LIMIT = 2;

    private final EventService eventService;
    private final ArchiveService archiveService;
    private final NoticeService noticeService;
    private final ItemService itemService;
    private final FeedbackService feedbackService;
    private final FileService fileService;

    public SearchResponse search(String keyword) {
        PreviewResult<EventSummary> events = eventService.searchPreview(keyword, PREVIEW_LIMIT);
        PreviewResult<Notice> notices = noticeService.searchPreview(keyword, PREVIEW_LIMIT);
        PreviewResult<ArchiveSummary> archives = archiveService.searchPreview(keyword, PREVIEW_LIMIT);

        // 행사·공지·아카이빙 썸네일을 모아 파일을 한 번에 조회한다. 섹션마다 따로 읽으면 쿼리가 섹션 수만큼 는다
        Map<Long, File> filesById = fileService.findAllByIdIn(thumbnailIds(events, notices, archives));

        return SearchResponse.of(
            PreviewResponse.from(events, summary -> EventListItemResponse.from(summary, filesById)),
            PreviewResponse.from(notices, notice -> NoticeListItemResponse.from(notice, filesById)),
            PreviewResponse.from(feedbackService.searchPreview(keyword, PREVIEW_LIMIT), FeedbackResponse::from),
            PreviewResponse.from(itemService.searchPreview(keyword, PREVIEW_LIMIT), ItemListItemResponse::from),
            PreviewResponse.from(archives, summary -> ArchiveListItemResponse.from(summary, filesById))
        );
    }

    private static List<Long> thumbnailIds(
        PreviewResult<EventSummary> events,
        PreviewResult<Notice> notices,
        PreviewResult<ArchiveSummary> archives
    ) {
        return Stream.of(
                events.content().stream().map(EventSummary::thumbnailFileId),
                notices.content().stream().map(Notice::getThumbnailImageId),
                archives.content().stream().map(ArchiveSummary::thumbnailFileId))
            .flatMap(Function.identity())
            .filter(Objects::nonNull)
            .toList();
    }
}
