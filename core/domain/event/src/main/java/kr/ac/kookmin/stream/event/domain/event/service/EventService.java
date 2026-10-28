package kr.ac.kookmin.stream.event.domain.event.service;

import kr.ac.kookmin.stream.common.CursorSliceResult;
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

    EventDetail getPublishedEvent(Long eventId);
}
