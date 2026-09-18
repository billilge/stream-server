package kr.ac.kookmin.stream.event.domain.event.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;

public interface EventRepository {

    Optional<Event> findById(Long id);

    /**
     * 주어진 식별자의 행사를 조회한다. 삭제 여부로 거르지 않는다.
     * <p>
     * 내 신청 내역처럼 이미 일어난 일을 보여주는 조회에 쓴다. 행사가 삭제돼도 신청 기록은 남아야 하고,
     * 여기서 걸러내면 커서 페이지의 크기 계산이 어긋난다.
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
     * 행사별 유효 신청자 수를 한 번에 조회한다. 목록처럼 여러 행사의 신청자 수가 필요할 때 N+1을 피하기 위한 것으로,
     * 신청이 한 건도 없는 행사는 결과에 담기지 않는다.
     */
    Map<Long, Long> countAppliedByEventIds(List<Long> eventIds);

    List<EventQuestion> findQuestionsByEventId(Long eventId);

    long countAppliedByEventId(Long eventId);

    boolean existsAppliedByEventIdAndMemberId(Long eventId, Long memberId);

    EventApplication saveApplication(EventApplication application);

    List<EventApplicationAnswer> saveAnswers(List<EventApplicationAnswer> answers);

    /**
     * 한 회원의 신청 내역을 신청 시각 내림차순(동일 시각은 applicationId 내림차순)으로 {@code limit}건까지 조회한다.
     * <p>
     * 페이지를 어디서 끊을지는 호출부가 정한다. 여기서는 요청한 개수만큼 돌려줄 뿐이다.
     */
    List<EventApplication> findApplicationSlice(Long memberId, EventApplicationCursor cursor, int limit);

    /**
     * 본인 신청 한 건을 조회한다. 남의 신청은 없는 것으로 보여야 하므로 회원 식별자를 조건에 함께 건다.
     */
    Optional<EventApplication> findApplicationByIdAndMemberId(Long applicationId, Long memberId);

    List<EventApplicationAnswer> findAnswersByApplicationId(Long applicationId);
}
