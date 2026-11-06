package kr.ac.kookmin.stream.api.app.event.event.usecase;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 행사 상세의 학생회비 신청 자격. 앱 납부 요청 기록이 아니라 신청과 같은 기준인 납부자 명부로 판정하는지 본다.
 */
class EventDetailUseCaseTest {

    private static final Long EVENT_ID = 15L;
    private static final Long MEMBER_ID = 100L;
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 4, 10, 0);

    private final FakeEventService eventService = new FakeEventService();
    private final FakeEventApplicationService eventApplicationService = new FakeEventApplicationService(eventService);
    private final FakePayerService payerService = new FakePayerService();
    private final EventDetailUseCase useCase =
        new EventDetailUseCase(eventService, eventApplicationService, payerService);

    private static Event event(boolean feePaidOnly) {
        return Event.of(
            EVENT_ID, "체육대회", "설명", "종합운동장", NOW.plusDays(10), NOW.plusDays(10).plusHours(2),
            NOW.minusDays(1), NOW.plusDays(3), RecruitType.OPEN, List.of(), 0, feePaidOnly, true, 7L, NOW
        );
    }

    @Test
    @DisplayName("납부자 전용이 아니면 납부자 명부에 없어도 신청 자격이 있다")
    void eligibleWhenNotFeePaidOnly() {
        eventService.events.put(EVENT_ID, event(false));

        assertTrue(useCase.getEvent(EVENT_ID, MEMBER_ID).feeEligible());
    }

    @Test
    @DisplayName("납부자 전용이면 납부자 명부에 있을 때 신청 자격이 있다")
    void eligibleWhenPayer() {
        eventService.events.put(EVENT_ID, event(true));
        payerService.payerIds.add(MEMBER_ID);

        assertTrue(useCase.getEvent(EVENT_ID, MEMBER_ID).feeEligible());
    }

    @Test
    @DisplayName("납부자 전용이면 납부자 명부에 없을 때 신청 자격이 없다")
    void notEligibleWhenNotPayer() {
        eventService.events.put(EVENT_ID, event(true));

        assertFalse(useCase.getEvent(EVENT_ID, MEMBER_ID).feeEligible());
    }
}
