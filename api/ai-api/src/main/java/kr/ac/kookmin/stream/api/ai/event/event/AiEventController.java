package kr.ac.kookmin.stream.api.ai.event.event;

import java.util.ArrayList;
import java.util.List;
import kr.ac.kookmin.stream.api.ai.event.event.response.AiEventResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventSummary;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ai/events")
@RequiredArgsConstructor
public class AiEventController implements AiEventApi {

    private static final int PAGE_SIZE = 100;

    private final EventService eventService;

    /**
     * 학생 목록과 같은 서비스로 게시된 행사 ID를 모은 뒤, 소개글·장소가 있는 행사 본체를 한 번에 가져온다.
     * 목록 요약에는 소개글이 없어서 두 단계로 나눴다. 둘 다 게시된 행사만 돌려준다.
     */
    @Override
    @GetMapping
    public ApiResponse<List<AiEventResponse>> getEvents() {
        List<Long> eventIds = new ArrayList<>();
        EventCursor cursor = null;
        do {
            CursorSliceResult<EventSummary> page = eventService.getPublishedEvents(null, null, cursor, PAGE_SIZE);
            page.content().forEach(summary -> eventIds.add(summary.eventId()));
            cursor = page.hasNext() ? EventCursor.from(page.nextCursor()) : null;
        } while (cursor != null);

        return ApiResponse.success(
            eventService.getPublishedByIds(eventIds).stream().map(AiEventResponse::from).toList());
    }
}
