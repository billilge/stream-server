package kr.ac.kookmin.stream.event.domain.event.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCreateCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventManageDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestionCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventUpdateCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitPolicy;
import kr.ac.kookmin.stream.event.domain.event.repository.EventApplicationRepository;
import kr.ac.kookmin.stream.event.domain.event.repository.EventRepository;
import kr.ac.kookmin.stream.event.domain.event.service.AdminEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final EventApplicationRepository eventApplicationRepository;

    // 질문 규칙은 행사를 저장한 뒤 질문을 만들 때 검사한다. 어기면 트랜잭션이 행사 저장까지 되돌린다
    @Override
    @Transactional
    public Event create(Long createdBy, EventCreateCommand command) {
        Event event = eventRepository.save(Event.create(createdBy, command));
        eventRepository.saveQuestions(toQuestions(event.getId(), command.questions()));
        return event;
    }

    @Override
    public PageResult<Event> getEvents(String keyword, PageOffset pageOffset) {
        return eventRepository.search(keyword, pageOffset);
    }

    @Override
    public Map<Long, Long> getApplicantCounts(List<Long> eventIds) {
        return eventApplicationRepository.countAppliedByEventIds(eventIds);
    }

    @Override
    public EventManageDetail getEvent(Long eventId) {
        Event event = getEventOrThrow(eventId);
        return new EventManageDetail(
            event,
            eventRepository.findQuestionsByEventId(eventId),
            eventApplicationRepository.countAppliedByEventId(eventId)
        );
    }

    @Override
    @Transactional
    public Event update(Long eventId, EventUpdateCommand command) {
        Event event = getEventOrThrow(eventId);
        validateNotModified(event, command.expectedUpdatedAt());
        // 질문 규칙(입력 형식)은 정원 하한처럼 DB 상태를 보는 검사보다 먼저 건다
        List<EventQuestion> questions = toQuestions(eventId, command.questions());
        validateCapacityNotBelowApplicants(eventId, command.recruitPolicy());

        if (questionsChanged(eventRepository.findQuestionsByEventId(eventId), questions)) {
            validateQuestionsEditable(eventId);
            eventRepository.replaceQuestions(eventId, questions);
            eventRepository.touchUpdatedAt(eventId);
        }

        event.update(command);
        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public Event changePublished(Long eventId, boolean published) {
        Event event = getEventOrThrow(eventId);
        if (published) {
            validatePublishable(event);
        }
        event.changePublished(published);
        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public void delete(Long eventId) {
        if (!eventRepository.delete(eventId)) {
            throw new BusinessException(EventErrorCode.EVENT_NOT_FOUND);
        }
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new BusinessException(EventErrorCode.EVENT_NOT_FOUND));
    }

    // updated_at이 초 단위 DATETIME이라 같은 초로 맞춰 비교한다. 수정 화면이 읽은 뒤 고쳐졌다면 시각이 달라진다
    private void validateNotModified(Event event, LocalDateTime expectedUpdatedAt) {
        LocalDateTime current = event.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS);
        if (!current.equals(expectedUpdatedAt.truncatedTo(ChronoUnit.SECONDS))) {
            throw new BusinessException(EventErrorCode.EVENT_UPDATE_CONFLICT);
        }
    }

    // 이미 받은 신청을 정원 밖으로 밀어낼 수는 없다
    private void validateCapacityNotBelowApplicants(Long eventId, RecruitPolicy recruitPolicy) {
        if (recruitPolicy.isCapacityBelow(eventApplicationRepository.countAppliedByEventId(eventId))) {
            throw new BusinessException(EventErrorCode.EVENT_CAPACITY_BELOW_APPLICANTS);
        }
    }

    // 학생 앱이 아직 납부자 전용 신청 제한과 기타 입력칸을 지원하지 않아, 공개하면 운영진 설정대로 동작하지 않는다.
    // 학생 쪽이 지원되면 이 검사를 지운다. 비공개로 돌리는 건 언제든 된다
    private void validatePublishable(Event event) {
        if (event.isFeePaidOnly() || eventRepository.findQuestionsByEventId(event.getId()).stream()
            .anyMatch(EventQuestion::isOtherOptionAllowed)) {
            throw new BusinessException(EventErrorCode.EVENT_UNSUPPORTED_OPTION);
        }
    }

    // 답변이 질문 id·선택지 순서로 저장돼 있어 신청이 생긴 뒤 바꾸면 기존 답변의 뜻이 달라진다
    private void validateQuestionsEditable(Long eventId) {
        if (eventApplicationRepository.existsByEventId(eventId)) {
            throw new BusinessException(EventErrorCode.EVENT_QUESTIONS_LOCKED);
        }
    }

    private static boolean questionsChanged(List<EventQuestion> current, List<EventQuestion> requested) {
        if (current.size() != requested.size()) {
            return true;
        }
        return IntStream.range(0, current.size())
            .anyMatch(index -> !current.get(index).hasSameContentAs(requested.get(index)));
    }

    // 신청서에 보이는 순서는 요청 목록의 순서를 따른다
    private static List<EventQuestion> toQuestions(Long eventId, List<EventQuestionCommand> commands) {
        return IntStream.range(0, commands.size())
            .mapToObj(index -> EventQuestion.create(eventId, index + 1, commands.get(index)))
            .toList();
    }
}
