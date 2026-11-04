package kr.ac.kookmin.stream.api.app.search.response;

import kr.ac.kookmin.stream.api.app.event.archive.response.ArchiveListItemResponse;
import kr.ac.kookmin.stream.api.app.event.event.response.EventListItemResponse;
import kr.ac.kookmin.stream.api.app.welfare.feedback.response.FeedbackResponse;
import kr.ac.kookmin.stream.api.app.welfare.notice.response.NoticeListItemResponse;
import kr.ac.kookmin.stream.api.app.welfare.rental.response.ItemListItemResponse;
import kr.ac.kookmin.stream.api.common.dto.PreviewResponse;

/**
 * 통합 검색 결과. 섹션마다 앞의 몇 건과 전체 수를 담는다. 섹션이 비어도 필드는 빠지지 않는다.
 * <p>
 * 카드는 각 목록 API의 항목 응답을 그대로 쓴다. 섹션을 어떤 순서로 그릴지는 화면이 정한다.
 *
 * @param totalCount 다섯 섹션 전체 수의 합. 0이면 검색 결과가 없다
 */
public record SearchResponse(
    long totalCount,
    PreviewResponse<EventListItemResponse> events,
    PreviewResponse<NoticeListItemResponse> notices,
    PreviewResponse<FeedbackResponse> feedbacks,
    PreviewResponse<ItemListItemResponse> items,
    PreviewResponse<ArchiveListItemResponse> archives
) {

    public static SearchResponse of(
        PreviewResponse<EventListItemResponse> events,
        PreviewResponse<NoticeListItemResponse> notices,
        PreviewResponse<FeedbackResponse> feedbacks,
        PreviewResponse<ItemListItemResponse> items,
        PreviewResponse<ArchiveListItemResponse> archives
    ) {
        long totalCount = events.totalCount() + notices.totalCount() + feedbacks.totalCount()
            + items.totalCount() + archives.totalCount();
        return new SearchResponse(totalCount, events, notices, feedbacks, items, archives);
    }
}
