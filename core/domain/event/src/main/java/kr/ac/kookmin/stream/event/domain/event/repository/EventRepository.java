package kr.ac.kookmin.stream.event.domain.event.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;

public interface EventRepository {

    Optional<Event> findById(Long id);

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
}
