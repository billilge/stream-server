package kr.ac.kookmin.stream.event.domain.event.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;

/**
 * 행사 신청과 신청서 답변 저장소. 행사·신청서 질문은 {@link EventRepository}가 맡는다.
 */
public interface EventApplicationRepository {

    EventApplication save(EventApplication application);

    List<EventApplicationAnswer> saveAnswers(List<EventApplicationAnswer> answers);

    /**
     * 행사별 유효 신청자 수를 한 번에 조회한다. 목록처럼 여러 행사의 신청자 수가 필요할 때 N+1을 피하기 위한 것으로,
     * 신청이 한 건도 없는 행사는 결과에 담기지 않는다.
     */
    Map<Long, Long> countAppliedByEventIds(List<Long> eventIds);

    long countAppliedByEventId(Long eventId);

    /** 한 행사의 신청 수. 취소된 신청도 센다. */
    long countByEventId(Long eventId);

    /**
     * 행사에 신청이 한 건이라도 있는지. 취소된 신청도 센다 — 취소돼도 답변이 질문 id·선택지 순서를 참조한다.
     */
    boolean existsByEventId(Long eventId);

    boolean existsAppliedByEventIdAndMemberId(Long eventId, Long memberId);

    /**
     * 행사의 유효 신청(취소하지 않은 신청)을 낸 회원 식별자.
     */
    List<Long> findAppliedMemberIds(Long eventId);

    /**
     * 한 회원의 신청 내역을 신청 시각 내림차순(동일 시각은 applicationId 내림차순)으로 {@code limit}건까지 조회한다.
     * <p>
     * 페이지를 어디서 끊을지는 호출부가 정한다. 여기서는 요청한 개수만큼 돌려줄 뿐이다.
     */
    List<EventApplication> findSliceByMemberId(Long memberId, EventApplicationCursor cursor, int limit);

    /**
     * 한 행사의 신청을 신청 시각 오름차순(동일 시각은 applicationId 오름차순)으로 페이지 조회한다. 취소된 신청도 담는다.
     *
     * @param status    {@code null}이면 상태로 거르지 않는다
     * @param memberIds {@code null}이면 신청자로 거르지 않는다
     */
    PageResult<EventApplication> searchByEventId(
        Long eventId,
        EventApplicationStatus status,
        List<Long> memberIds,
        PageOffset pageOffset
    );

    /**
     * 회원이 신청한 행사 중 아직 지나지 않은 것을 행사 시작이 임박한 순으로 {@code limit}건까지 조회한다.
     * 취소한 신청과 삭제된 행사는 뺀다.
     *
     * @param startOfToday 오늘 0시. 시작 시각이 아니라 날짜로 거른다 — 당일 행사는 이미 시작했어도 보여줘야 한다
     */
    List<Event> findUpcomingAppliedEvents(Long memberId, LocalDateTime startOfToday, int limit);

    /**
     * {@link #findUpcomingAppliedEvents}와 같은 조건의 전체 수. 홈이 "N건 더보기"를 그릴 때 쓴다.
     */
    long countUpcomingAppliedEvents(Long memberId, LocalDateTime startOfToday);

    /**
     * 본인 신청 한 건을 조회한다. 남의 신청은 없는 것으로 보여야 하므로 회원 식별자를 조건에 함께 건다.
     */
    Optional<EventApplication> findByIdAndMemberId(Long applicationId, Long memberId);

    /** 한 행사에 속한 신청 한 건을 조회한다. 다른 행사의 신청은 없는 것으로 본다. */
    Optional<EventApplication> findByIdAndEventId(Long applicationId, Long eventId);

    List<EventApplicationAnswer> findAnswersByApplicationId(Long applicationId);

    /** 여러 신청의 답변을 한 번에 조회한다. 신청마다 따로 읽으면 페이지 크기만큼 쿼리가 더 나간다. */
    List<EventApplicationAnswer> findAnswersByApplicationIds(List<Long> applicationIds);
}
