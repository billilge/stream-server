package kr.ac.kookmin.stream.event.domain.event.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.common.PreviewResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationForm;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationSummary;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplyCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.UpcomingAppliedEvent;

/**
 * 행사 신청과 그 이력. 행사 자체의 조회는 {@link EventService}가, 운영진 신청 현황은 {@link AdminEventApplicationService}가
 * 맡는다.
 */
public interface EventApplicationService {

    EventApplicationForm getApplicationForm(Long eventId);

    EventApplicationResult apply(Long eventId, Long memberId, EventApplyCommand command);

    CursorSliceResult<EventApplicationSummary> getApplicationsByMemberId(
        Long memberId,
        EventApplicationCursor cursor,
        int size
    );

    /**
     * 회원이 신청한 행사 중 아직 끝나지 않은 것을 행사 시작 임박순으로 {@code limit}건까지, 전체 수와 함께 조회한다.
     * <p>
     * 홈이 "신청한 행사 · N건"과 미리보기 두 건을 그리는 데 쓴다. 신청 내역 화면이 쓰는
     * {@link #getApplicationsByMemberId}는 신청 시각 최신순 커서 조회라 전체 수를 주지 않고, 행사일도 담지 않는다.
     * 취소한 신청과 끝난 행사는 빼고 신청과 행사 자체를 돌려준다 — 화면이 어떤 필드를 쓸지는 응답이 고른다. 진행 중인
     * 행사는 남으므로 "아직 열리지 않은 것"이 아니다.
     */
    PreviewResult<UpcomingAppliedEvent> getUpcomingAppliedEvents(Long memberId, int limit);

    /**
     * 주어진 행사 중 회원이 신청한 것의 식별자. 취소한 신청은 빼고 본다.
     */
    Set<Long> getAppliedEventIds(Long memberId, Collection<Long> eventIds);

    /**
     * 주어진 행사 중 회원이 낸 유효 신청을 행사 식별자별로 조회한다. {@link #getAppliedEventIds}와 같은 조건이되 신청
     * 자체가 필요할 때 쓴다 — 행사 목록·상세가 신청 식별자로 신청 내역 상세를 연다.
     * <p>
     * 신청하지 않은 행사는 결과에 담기지 않는다. 같은 행사에 유효 신청이 둘 이상이면 나중 것(식별자가 큰 것)을 쓴다.
     */
    Map<Long, EventApplication> getAppliedApplications(Long memberId, Collection<Long> eventIds);

    EventApplicationDetail getApplicationByIdAndMemberId(Long applicationId, Long memberId);

    void cancelApplication(Long applicationId, Long memberId);
}
