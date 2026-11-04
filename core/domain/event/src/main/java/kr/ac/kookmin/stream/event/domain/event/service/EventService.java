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

    /**
     * 게시되고 삭제되지 않은 행사 중 식별자에 해당하는 것. 순서는 보장하지 않는다.
     * <p>
     * 다른 화면이 행사를 곁들여 보여줄 때(홈 배너의 연결 행사) 그 행사가 아직 학생에게 보이는지 한 번에
     * 확인하는 데 쓴다. 한 건을 보려면 {@link #getByIdOrNull(Long)}를 쓰는데, 그쪽은 미게시도 돌려준다.
     */
    List<Event> getPublishedByIds(Collection<Long> eventIds);

    EventDetail getPublishedEvent(Long eventId);

    /**
     * 지금 신청할 수 있는 행사 전부를 신청 마감 임박순으로 조회한다. 거르는 기준은
     * {@link #getApplicableEvents(Collection)}과 같고, 운영진 홈 관리의 노출 후보용이다.
     */
    List<Event> getAllApplicableEvents();

    /**
     * 행사 한 건. 없거나 삭제됐으면 {@code null}이다. <b>아직 게시하지 않은 행사도 돌려준다</b> — 운영진 화면은
     * 미게시 행사도 보여야 하고, 학생에게 감추는 일은 부르는 쪽이 {@link Event#isPublished()}로 한다.
     * <p>
     * 다른 화면이 행사를 곁들여 보여줄 때(아카이빙·홈 배너의 연결 행사) 쓴다. 행사가 사라졌다고 그 화면까지
     * 404가 되면 안 되므로 {@link #getPublishedEvent(Long)}와 달리 던지지 않는다.
     */
    Event getByIdOrNull(Long eventId);
}
