package kr.ac.kookmin.stream.event.domain.event.service;

import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.common.PreviewResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationForm;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationSummary;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplyCommand;

/**
 * 행사 신청과 그 이력. 행사 자체의 조회는 {@link EventService}가, 운영진 신청 현황은 {@link AdminEventApplicationService}가
 * 맡는다.
 */
public interface EventApplicationService {

    EventApplicationForm getApplicationForm(Long eventId);

    EventApplicationResult apply(Long eventId, Long memberId, EventApplyCommand command);

    CursorSliceResult<EventApplicationSummary> getMyApplications(
        Long memberId,
        EventApplicationCursor cursor,
        int size
    );

    /**
     * 회원이 신청한 행사 중 아직 열리지 않은 것을 행사 시작 임박순으로 {@code limit}건까지, 전체 수와 함께 조회한다.
     * <p>
     * 홈이 "신청한 행사 · N건"과 미리보기 두 건을 그리는 데 쓴다. 신청 내역 화면이 쓰는
     * {@link #getMyApplications}는 신청 시각 최신순 커서 조회라 전체 수를 주지 않고, 행사일도 담지 않는다.
     * 취소한 신청과 지난 행사는 빼고 행사 자체를 돌려준다 — 화면이 어떤 필드를 쓸지는 응답이 고른다.
     */
    PreviewResult<Event> getUpcomingAppliedEvents(Long memberId, int limit);

    EventApplicationDetail getMyApplication(Long applicationId, Long memberId);

    void cancelApplication(Long applicationId, Long memberId);
}
