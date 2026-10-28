package kr.ac.kookmin.stream.event.domain.event.service;

import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCreateCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventManageDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventUpdateCommand;

/**
 * 운영진용 행사 서비스. 비공개 행사를 포함한 목록·상세 조회와 등록·수정을 맡는다. 학생 앱의 공개 행사 조회는
 * {@link EventService}가 맡는다.
 */
public interface AdminEventService {

    /**
     * 운영진이 행사를 등록한다. 신청서 질문도 함께 저장하며, 등록한 행사는 비공개로 시작한다.
     */
    Event create(Long createdBy, EventCreateCommand command);

    /**
     * 운영진 행사 목록. 비공개 행사도 담는다. 신청자 수는 {@link #getApplicantCounts}로 따로 조회해 조합한다.
     *
     * @param keyword 제목 부분 일치 검색어. null이거나 공백이면 거르지 않는다. 앞뒤 공백은 지우고 {@code %}·{@code _}는 글자
     *                그대로 검색한다
     */
    PageResult<Event> getEvents(String keyword, PageOffset pageOffset);

    /**
     * 행사별 유효(APPLIED) 신청자 수. 신청이 한 건도 없는 행사는 결과에 담기지 않는다.
     */
    Map<Long, Long> getApplicantCounts(List<Long> eventIds);

    /** 운영진 행사 상세. 비공개 행사도 조회한다. */
    EventManageDetail getEvent(Long eventId);

    /**
     * 운영진이 행사 내용을 통째로 바꾼다. 수정 화면이 읽은 뒤 다른 운영진이 먼저 고쳤다면
     * {@code EVENT_UPDATE_CONFLICT}로 거부한다. 신청이 있는 행사는 신청서 질문을 바꿀 수 없고,
     * 선착순 정원을 현재 신청자 수보다 줄일 수 없다.
     */
    Event update(Long eventId, EventUpdateCommand command);
}
