package kr.ac.kookmin.stream.event.domain.event.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;

/**
 * 행사와 신청서 질문 저장소. 신청·답변은 {@link EventApplicationRepository}가 맡는다.
 */
public interface EventRepository {

    Optional<Event> findById(Long id);

    Event save(Event event);

    /**
     * 행사를 삭제 표시한다(soft delete). 신청·답변은 남긴다.
     *
     * @return 삭제할 행사가 있었는지. 없거나 이미 삭제됐으면 {@code false}
     */
    boolean delete(Long id);

    List<EventQuestion> saveQuestions(List<EventQuestion> questions);

    /** 행사의 신청서 질문을 모두 지우고 주어진 질문으로 바꾼다. */
    List<EventQuestion> replaceQuestions(Long eventId, List<EventQuestion> questions);

    /**
     * 행사의 수정 시각을 지금으로 갱신한다. 신청서 질문만 바뀌면 events 행의 값이 그대로라
     * DB가 updated_at을 올리지 않으므로, 동시 수정 감지를 위해 직접 올린다.
     */
    void touchUpdatedAt(Long eventId);

    /**
     * 삭제되지 않은 행사를 공개 여부와 상관없이 최근 등록 순(eventId 내림차순)으로 페이지 조회한다. 운영진 목록용이다.
     *
     * @param keyword 제목 부분 일치 검색어. {@code null}이면 거르지 않는다
     */
    PageResult<Event> findPage(String keyword, PageOffset pageOffset);

    /**
     * 주어진 식별자의 행사를 조회한다. 삭제 여부로 거르지 않는다.
     * <p>
     * 내 신청 내역에서 신청마다 행사 정보를 붙일 때 쓴다. 여기서 걸러내면 커서 페이지의 크기 계산이 어긋난다.
     */
    List<Event> findAllByIds(List<Long> eventIds);

    /**
     * 게시되고 삭제되지 않은 행사 한 건을 조회한다. 아직 공개하지 않은 행사는 학생에게 없는 것으로 보여야 한다.
     */
    Optional<Event> findPublishedById(Long id);

    /**
     * 게시되고 삭제되지 않은 행사를 행사 시작 일시 오름차순(동일 시각은 eventId 오름차순)으로 {@code limit}건까지 조회한다.
     * recruitStatus가 주어지면 {@code now} 기준으로 계산한 모집 상태가 일치하는 행사만 남긴다.
     * <p>
     * 페이지를 어디서 끊을지는 호출부가 정한다. 여기서는 요청한 개수만큼 돌려줄 뿐이다.
     */
    List<Event> findPublishedSlice(
        RecruitStatus recruitStatus,
        EventCursor cursor,
        int limit,
        LocalDateTime now
    );

    /**
     * 게시되고 삭제되지 않은 행사 중 시작 일시가 {@code [from, to)}인 행사. 행사 전날 알림 대상을 고를 때 쓴다.
     */
    List<Event> findPublishedStartingBetween(LocalDateTime from, LocalDateTime to);

    /**
     * 게시되고 삭제되지 않은 행사 중 신청 마감 일시가 {@code [from, to)}인 행사. 모집 마감 임박 알림 대상을 고를 때 쓴다.
     */
    List<Event> findPublishedApplyEndingBetween(LocalDateTime from, LocalDateTime to);

    List<EventQuestion> findQuestionsByEventId(Long eventId);
}
