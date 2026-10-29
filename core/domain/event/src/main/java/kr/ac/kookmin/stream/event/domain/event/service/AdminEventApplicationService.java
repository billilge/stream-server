package kr.ac.kookmin.stream.event.domain.event.service;

import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;

/**
 * 운영진용 행사 신청 서비스. 신청 현황과 신청자 상세 조회를 맡는다. 학생의 신청·취소·내 신청 조회는
 * {@link EventApplicationService}가 맡는다.
 */
public interface AdminEventApplicationService {

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

    /**
     * 운영진 신청자 상세. 그 행사에 속한 신청만 조회한다.
     */
    EventApplication getApplication(Long eventId, Long applicationId);
}
