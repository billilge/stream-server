package kr.ac.kookmin.stream.api.app.event.event.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplyCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 행사 신청 앞의 납부자 확인. 모집 기간·정원·답변 검증은 신청 서비스의 몫이라 가짜로 대신하고, 여기서는 납부자 전용
 * 행사에서 납부자 명부를 확인하는지만 본다.
 */
class EventApplyUseCaseTest {

    private static final Long EVENT_ID = 15L;
    private static final Long MEMBER_ID = 100L;
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 4, 10, 0);
    private static final EventApplyCommand COMMAND = new EventApplyCommand(List.of());

    private final FakeEventService eventService = new FakeEventService();
    private final FakeEventApplicationService eventApplicationService = new FakeEventApplicationService(eventService);
    private final FakePayerService payerService = new FakePayerService();
    private final EventApplyUseCase useCase =
        new EventApplyUseCase(eventService, eventApplicationService, payerService);

    private static Event event(boolean feePaidOnly, boolean published) {
        return Event.of(
            EVENT_ID, "체육대회", "설명", "종합운동장", NOW.plusDays(10), NOW.plusDays(10).plusHours(2),
            NOW.minusDays(1), NOW.plusDays(3), RecruitType.OPEN, List.of(), 0, feePaidOnly, published, 7L, NOW
        );
    }

    private void assertRejected(ErrorCode expected) {
        BusinessException e = assertThrows(BusinessException.class, () -> useCase.apply(EVENT_ID, MEMBER_ID, COMMAND));
        assertEquals(expected, e.getErrorCode());
    }

    @Test
    @DisplayName("납부자 전용 행사에 납부자 명부에 없는 학생이 신청하면 거부하고 신청을 접수하지 않는다")
    void rejectsNonPayer() {
        eventService.events.put(EVENT_ID, event(true, true));

        assertRejected(FeeErrorCode.MEMBER_IS_NOT_PAYER);
        assertTrue(eventApplicationService.appliedMemberIds.isEmpty());
    }

    @Test
    @DisplayName("납부자 전용 행사라도 납부자 명부에 있는 학생은 신청할 수 있다")
    void acceptsPayer() {
        eventService.events.put(EVENT_ID, event(true, true));
        payerService.payerIds.add(MEMBER_ID);

        useCase.apply(EVENT_ID, MEMBER_ID, COMMAND);

        assertEquals(List.of(MEMBER_ID), eventApplicationService.appliedMemberIds);
    }

    @Test
    @DisplayName("납부자 전용이 아닌 행사는 납부자 명부에 없어도 신청할 수 있다")
    void acceptsAnyoneWhenNotFeePaidOnly() {
        eventService.events.put(EVENT_ID, event(false, true));

        useCase.apply(EVENT_ID, MEMBER_ID, COMMAND);

        assertEquals(List.of(MEMBER_ID), eventApplicationService.appliedMemberIds);
    }

    @Test
    @DisplayName("게시 전인 납부자 전용 행사는 미납 거부가 아니라 찾을 수 없음으로 답한다 — 행사가 있다는 사실을 드러내지 않는다")
    void unpublishedEventIsNotFound() {
        eventService.events.put(EVENT_ID, event(true, false));

        assertRejected(EventErrorCode.EVENT_NOT_FOUND);
    }
}
