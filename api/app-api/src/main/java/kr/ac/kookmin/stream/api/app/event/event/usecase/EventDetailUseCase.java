package kr.ac.kookmin.stream.api.app.event.event.usecase;

import kr.ac.kookmin.stream.event.domain.event.domain.EventDetail;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import kr.ac.kookmin.stream.welfare.domain.fee.service.PayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 행사 상세에 요청한 학생의 학생회비 신청 자격을 붙인다. 행사({@code event})와 납부자 명부({@code welfare})는 도메인이
 * 달라 이 UseCase가 조합한다({@code architecture.md} 6-1절).
 * <p>
 * 앱 납부 요청 기록만 보는 {@code GET /v1/app/fee/me}로 판단하면, 명부에만 있는 레거시 납부자처럼 서버는 신청을 받아 줄
 * 학생을 화면이 막게 된다. 그래서 신청({@link EventApplyUseCase})과 같은 기준인 납부자 명부로 판정해 내려준다.
 */
@Component
@RequiredArgsConstructor
public class EventDetailUseCase {

    private final EventService eventService;
    private final PayerService payerService;

    public EventDetailWithFeeEligibility getEvent(Long eventId, Long memberId) {
        EventDetail detail = eventService.getPublishedEvent(eventId);
        return new EventDetailWithFeeEligibility(detail, isFeeEligible(detail, memberId));
    }

    // 납부자 전용이 아니면 누구나 신청할 수 있어 명부를 조회하지 않는다
    private boolean isFeeEligible(EventDetail detail, Long memberId) {
        return !detail.feePaidOnly() || payerService.isPayer(memberId);
    }
}
