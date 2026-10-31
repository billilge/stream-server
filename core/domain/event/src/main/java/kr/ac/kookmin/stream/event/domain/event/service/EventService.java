package kr.ac.kookmin.stream.event.domain.event.service;

import java.util.Collection;
import java.util.List;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventSummary;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;

/**
 * 학생 앱의 공개 행사 조회. 운영진의 조회·등록·수정은 {@link AdminEventService}, 신청·취소는
 * {@link EventApplicationService}가 맡는다.
 */
public interface EventService {

    CursorSliceResult<EventSummary> getPublishedEvents(RecruitStatus recruitStatus, EventCursor cursor, int size);

    /**
     * 주어진 행사 중 지금 신청할 수 있는 것을 신청 마감 임박순으로 조회한다.
     * 미게시·삭제된 행사와 신청 기간이 아니거나 정원이 찬 행사는 빠진다.
     */
    List<Event> getApplicableEvents(Collection<Long> eventIds);

    EventDetail getPublishedEvent(Long eventId);
}
