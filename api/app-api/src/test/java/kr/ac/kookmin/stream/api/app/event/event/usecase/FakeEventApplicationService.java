package kr.ac.kookmin.stream.api.app.event.event.usecase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.common.PreviewResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationForm;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationSummary;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplyCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.UpcomingAppliedEvent;
import kr.ac.kookmin.stream.event.domain.event.service.EventApplicationService;

/**
 * 행사 UseCase 테스트용 신청 서비스. 실제 서비스처럼 없거나 게시 전인 행사는 {@code EVENT_NOT_FOUND}로 거르고, 나머지는
 * 접수만 기록한다. 행사 상세가 묻는 내 신청은 없는 것으로 답하고, 이 테스트들이 쓰지 않는 조회는 막아 둔다.
 */
class FakeEventApplicationService implements EventApplicationService {

    private final FakeEventService eventService;
    final List<Long> appliedMemberIds = new ArrayList<>();

    FakeEventApplicationService(FakeEventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public EventApplicationForm getApplicationForm(Long eventId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EventApplicationResult apply(Long eventId, Long memberId, EventApplyCommand command) {
        Event event = eventService.getByIdOrNull(eventId);
        if (event == null || !event.isPublished()) {
            throw new BusinessException(EventErrorCode.EVENT_NOT_FOUND);
        }
        appliedMemberIds.add(memberId);
        return new EventApplicationResult(1L, event);
    }

    @Override
    public CursorSliceResult<EventApplicationSummary> getApplicationsByMemberId(
        Long memberId, EventApplicationCursor cursor, int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreviewResult<UpcomingAppliedEvent> getUpcomingAppliedEvents(Long memberId, int limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Long> getAppliedEventIds(Long memberId, Collection<Long> eventIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<Long, EventApplication> getAppliedApplications(Long memberId, Collection<Long> eventIds) {
        return Map.of();
    }

    @Override
    public EventApplicationDetail getApplicationByIdAndMemberId(Long applicationId, Long memberId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancelApplication(Long applicationId, Long memberId) {
        throw new UnsupportedOperationException();
    }
}
