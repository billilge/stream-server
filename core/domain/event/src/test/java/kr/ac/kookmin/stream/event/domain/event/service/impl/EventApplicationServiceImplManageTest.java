package kr.ac.kookmin.stream.event.domain.event.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 운영진 신청 현황·신청자 상세 조회. 신청자 이름·학번 조합은 api 계층 UseCase가 맡으므로 여기서는 신청 조회 규칙만 본다.
 */
class EventApplicationServiceImplManageTest {

    private static final Long EVENT_ID = 15L;
    private static final Long OTHER_EVENT_ID = 16L;
    private static final LocalDateTime APPLIED_AT = LocalDateTime.of(2026, 10, 3, 14, 0);

    private final InMemoryEventRepository repository = new InMemoryEventRepository();
    private final InMemoryEventApplicationRepository applicationRepository = new InMemoryEventApplicationRepository();
    private final EventApplicationServiceImpl service =
        new EventApplicationServiceImpl(repository, applicationRepository, new EventApplyAnswerValidator());

    private static Event event(Long id) {
        return Event.of(
            id, "새내기 배움터", "설명", "미래관", APPLIED_AT.plusDays(30), APPLIED_AT.plusDays(31),
            APPLIED_AT.minusDays(7), APPLIED_AT.plusDays(10), RecruitType.FIRST_COME, List.of(), 200,
            false, true, 1L, APPLIED_AT
        );
    }

    private void givenApplication(Long id, Long eventId, Long memberId, EventApplicationStatus status,
        LocalDateTime appliedAt) {
        applicationRepository.applications.put(id, EventApplication.of(id, eventId, memberId, status, appliedAt,
            status == EventApplicationStatus.CANCELED ? appliedAt.plusHours(1) : null));
    }

    private static List<Long> idsOf(PageResult<EventApplication> page) {
        return page.content().stream().map(EventApplication::getId).toList();
    }

    private static void assertRejected(EventErrorCode expected, Runnable action) {
        BusinessException e = assertThrows(BusinessException.class, action::run);
        assertEquals(expected, e.getErrorCode());
    }

    @BeforeEach
    void setUp() {
        repository.events.put(EVENT_ID, event(EVENT_ID));
        repository.events.put(OTHER_EVENT_ID, event(OTHER_EVENT_ID));
        // 신청 순서와 id 순서를 일부러 엇갈리게 둔다
        givenApplication(11L, EVENT_ID, 101L, EventApplicationStatus.APPLIED, APPLIED_AT.plusDays(1));
        givenApplication(12L, EVENT_ID, 102L, EventApplicationStatus.APPLIED, APPLIED_AT);
        givenApplication(13L, EVENT_ID, 103L, EventApplicationStatus.CANCELED, APPLIED_AT.plusDays(2));
        givenApplication(14L, OTHER_EVENT_ID, 101L, EventApplicationStatus.APPLIED, APPLIED_AT);
    }

    @Nested
    @DisplayName("신청 현황")
    class GetApplications {

        @Test
        @DisplayName("그 행사의 신청만, 신청 순으로, 취소된 신청까지 담는다")
        void ordersByAppliedAtAndKeepsCanceled() {
            PageResult<EventApplication> page = service.getApplications(EVENT_ID, null, null, PageOffset.of(0, 20));

            assertEquals(List.of(12L, 11L, 13L), idsOf(page));
            assertEquals(3, page.totalCount());
        }

        @Test
        @DisplayName("신청 상태로 거른다")
        void filtersByStatus() {
            assertEquals(List.of(12L, 11L), idsOf(service.getApplications(
                EVENT_ID, EventApplicationStatus.APPLIED, null, PageOffset.of(0, 20))));
            assertEquals(List.of(13L), idsOf(service.getApplications(
                EVENT_ID, EventApplicationStatus.CANCELED, null, PageOffset.of(0, 20))));
        }

        @Test
        @DisplayName("신청자(검색된 회원)로 거른다")
        void filtersByMembers() {
            assertEquals(List.of(11L, 13L), idsOf(service.getApplications(
                EVENT_ID, null, List.of(101L, 103L), PageOffset.of(0, 20))));
        }

        @Test
        @DisplayName("페이지 단위로 자른다")
        void pages() {
            PageResult<EventApplication> secondPage =
                service.getApplications(EVENT_ID, null, null, PageOffset.of(1, 2));

            assertEquals(List.of(13L), idsOf(secondPage));
            assertEquals(3, secondPage.totalCount());
            assertEquals(2, secondPage.totalPage());
        }

        @Test
        @DisplayName("없거나 삭제된 행사는 찾을 수 없다")
        void eventNotFound() {
            assertRejected(EventErrorCode.EVENT_NOT_FOUND,
                () -> service.getApplications(99L, null, null, PageOffset.of(0, 20)));
        }
    }

    @Test
    @DisplayName("전체 신청 수는 취소된 신청도 센다")
    void countsCanceledApplications() {
        assertEquals(3, service.countApplications(EVENT_ID));
    }

    @Test
    @DisplayName("답변은 신청별로 묶어 돌려주고, 답변이 없는 신청은 담지 않는다")
    void groupsAnswersByApplication() {
        applicationRepository.answers.add(EventApplicationAnswer.of(1L, 11L, 201L, "010-1234-5678", null));
        applicationRepository.answers.add(EventApplicationAnswer.of(2L, 11L, 202L, null, List.of(0)));
        applicationRepository.answers.add(EventApplicationAnswer.of(3L, 12L, 201L, "010-2345-6789", null));

        Map<Long, List<EventApplicationAnswer>> answers = service.getAnswersByApplicationIds(List.of(11L, 12L, 13L));

        assertEquals(2, answers.get(11L).size());
        assertEquals(1, answers.get(12L).size());
        assertFalse(answers.containsKey(13L));
    }

    @Nested
    @DisplayName("신청자 상세")
    class GetApplication {

        @Test
        @DisplayName("그 행사의 신청을 돌려준다")
        void returnsApplication() {
            assertEquals(103L, service.getApplication(EVENT_ID, 13L).getMemberId());
        }

        @Test
        @DisplayName("다른 행사의 신청이면 찾을 수 없다")
        void otherEventsApplication() {
            assertRejected(EventErrorCode.APPLICATION_NOT_FOUND, () -> service.getApplication(EVENT_ID, 14L));
        }

        @Test
        @DisplayName("없거나 삭제된 행사는 신청보다 먼저 걸러진다")
        void eventNotFound() {
            repository.events.remove(EVENT_ID);

            assertRejected(EventErrorCode.EVENT_NOT_FOUND, () -> service.getApplication(EVENT_ID, 11L));
        }
    }
}
