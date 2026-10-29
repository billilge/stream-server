package kr.ac.kookmin.stream.event.domain.event.service;

import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventSummary;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;

/** 행사 자체의 조회. 신청·취소는 {@link EventApplicationService}가 맡는다. */
public interface EventService {

    CursorSliceResult<EventSummary> getPublishedEvents(RecruitStatus recruitStatus, EventCursor cursor, int size);

    EventDetail getPublishedEvent(Long eventId);

    /**
     * 행사 한 건. 없거나 삭제됐으면 {@code null}이다. <b>아직 게시하지 않은 행사도 돌려준다</b> — 운영진 화면은
     * 미게시 행사도 보여야 하고, 학생에게 감추는 일은 부르는 쪽이 {@link Event#isPublished()}로 한다.
     * <p>
     * 다른 화면이 행사를 곁들여 보여줄 때(아카이빙의 연결 행사) 쓴다. 행사가 사라졌다고 그 화면까지 404가 되면
     * 안 되므로 {@link #getPublishedEvent(Long)}와 달리 던지지 않는다.
     */
    Event getByIdOrNull(Long eventId);
}
