package kr.ac.kookmin.stream.event.domain.event.service;

import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationForm;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationSummary;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplyCommand;

/** 행사 신청과 그 이력. 행사 자체의 조회는 {@link EventService}가 맡는다. */
public interface EventApplicationService {

    EventApplicationForm getApplicationForm(Long eventId);

    EventApplicationResult apply(Long eventId, Long memberId, EventApplyCommand command);

    CursorSliceResult<EventApplicationSummary> getMyApplications(
        Long memberId,
        EventApplicationCursor cursor,
        int size
    );

    EventApplicationDetail getMyApplication(Long applicationId, Long memberId);

    void cancelApplication(Long applicationId, Long memberId);
}
