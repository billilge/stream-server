package kr.ac.kookmin.stream.api.app.event.event.usecase;

import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplyCommand;
import kr.ac.kookmin.stream.event.domain.event.service.EventApplicationService;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import kr.ac.kookmin.stream.welfare.domain.fee.service.PayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 행사 신청. 납부자 전용 행사면 납부자 명부({@code welfare})를 확인한 뒤 신청({@code event})을 접수한다.
 * <p>
 * 납부 확인은 읽기 하나라 신청과 원자적으로 묶을 필요가 없어 트랜잭션을 걸지 않는다. 신청을 저장하는 트랜잭션은
 * 신청 서비스가 연다.
 */
@Component
@RequiredArgsConstructor
public class EventApplyUseCase {

    private final EventService eventService;
    private final EventApplicationService eventApplicationService;
    private final PayerService payerService;

    public EventApplicationResult apply(Long eventId, Long memberId, EventApplyCommand command) {
        validatePayerIfRequired(eventId, memberId);
        return eventApplicationService.apply(eventId, memberId, command);
    }

    /**
     * 납부자 전용 행사면 신청자가 납부자 명부에 있는지 확인하고, 없으면 {@code MEMBER_IS_NOT_PAYER}로 거부한다.
     * <p>
     * 없거나 게시 전인 행사는 그냥 넘긴다. 신청 서비스가 {@code EVENT_NOT_FOUND}로 거르는데, 여기서 먼저 거부하면 게시 전
     * 행사가 있다는 사실이 드러난다.
     */
    private void validatePayerIfRequired(Long eventId, Long memberId) {
        Event event = eventService.getByIdOrNull(eventId);
        if (event != null && event.isPublished() && event.isFeePaidOnly()) {
            payerService.validatePayer(memberId);
        }
    }
}
