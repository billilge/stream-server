package kr.ac.kookmin.stream.event.domain.event.service;

import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCreateCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventManageDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventManageSummary;
import kr.ac.kookmin.stream.event.domain.event.domain.EventSummary;
import kr.ac.kookmin.stream.event.domain.event.domain.EventUpdateCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;

/** 행사 자체의 조회와 운영진의 등록·수정. 신청·취소는 {@link EventApplicationService}가 맡는다. */
public interface EventService {

    CursorSliceResult<EventSummary> getPublishedEvents(RecruitStatus recruitStatus, EventCursor cursor, int size);

    EventDetail getPublishedEvent(Long eventId);

    /**
     * 운영진이 행사를 등록한다. 신청서 질문도 함께 저장하며, 등록한 행사는 비공개로 시작한다.
     */
    Event create(Long createdBy, EventCreateCommand command);

    /**
     * 운영진 행사 목록. 비공개 행사도 담는다.
     *
     * @param keyword 제목 부분 일치 검색어. {@code null}이면 거르지 않는다
     */
    PageResult<EventManageSummary> getEvents(String keyword, PageOffset pageOffset);

    /** 운영진 행사 상세. 비공개 행사도 조회한다. */
    EventManageDetail getEvent(Long eventId);

    /**
     * 운영진이 행사 내용을 통째로 바꾼다. 수정 화면이 읽은 뒤 다른 운영진이 먼저 고쳤다면
     * {@code EVENT_UPDATE_CONFLICT}로 거부한다. 신청이 있는 행사는 신청서 질문을 바꿀 수 없고,
     * 선착순 정원을 현재 신청자 수보다 줄일 수 없다.
     */
    Event update(Long eventId, EventUpdateCommand command);
}
