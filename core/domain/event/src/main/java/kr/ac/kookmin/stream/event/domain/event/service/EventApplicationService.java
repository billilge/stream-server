package kr.ac.kookmin.stream.event.domain.event.service;

import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationForm;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationSummary;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplyCommand;

/** 행사 신청과 그 이력. 행사 자체의 조회는 {@link EventService}가 맡는다. */
public interface EventApplicationService {

    EventApplicationForm getApplicationForm(Long eventId);

    EventApplicationResult apply(Long eventId, Long memberId, EventApplyCommand command);

    CursorSliceResult<EventApplicationSummary> getMyApplications(
        Long memberId,
        EventApplicationCursor cursor,
        int size
    );

    EventApplicationDetail getMyApplication(Long applicationId, Long memberId);

    void cancelApplication(Long applicationId, Long memberId);

    /**
     * 운영진 신청 현황. 한 행사의 신청을 신청 순으로 페이지 조회하며, 취소된 신청도 담는다.
     *
     * @param status    {@code null}이면 상태로 거르지 않는다
     * @param memberIds {@code null}이면 신청자로 거르지 않는다
     */
    PageResult<EventApplication> getApplications(
        Long eventId,
        EventApplicationStatus status,
        List<Long> memberIds,
        PageOffset pageOffset
    );

    /** 신청별 답변. 답변을 하나도 저장하지 않은 신청은 결과에 담기지 않는다. */
    Map<Long, List<EventApplicationAnswer>> getAnswersByApplicationIds(List<Long> applicationIds);

    /** 한 행사의 신청 수. 취소된 신청도 센다. */
    long countApplications(Long eventId);
}
