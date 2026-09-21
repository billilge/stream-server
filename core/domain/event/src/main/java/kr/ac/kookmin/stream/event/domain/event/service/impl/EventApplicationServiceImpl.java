package kr.ac.kookmin.stream.event.domain.event.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationForm;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationSummary;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplyCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.event.domain.event.repository.EventRepository;
import kr.ac.kookmin.stream.event.domain.event.service.EventApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class EventApplicationServiceImpl implements EventApplicationService {

    private final EventRepository eventRepository;
    private final EventApplyAnswerValidator eventApplyAnswerValidator;

    @Override
    @Transactional(readOnly = true)
    public EventApplicationForm getApplicationForm(Long eventId) {
        Event event = getOpenEvent(eventId);
        return new EventApplicationForm(event, eventRepository.findQuestionsByEventId(eventId));
    }

    @Override
    @Transactional
    public EventApplicationResult apply(Long eventId, Long memberId, EventApplyCommand command) {
        Event event = getOpenEvent(eventId);

        if (eventRepository.existsAppliedByEventIdAndMemberId(eventId, memberId)) {
            throw new BusinessException(EventErrorCode.ALREADY_APPLIED);
        }

        List<EventQuestion> questions = eventRepository.findQuestionsByEventId(eventId);
        eventApplyAnswerValidator.validate(questions, command);

        EventApplication application = eventRepository.saveApplication(
            EventApplication.create(eventId, memberId, LocalDateTime.now())
        );
        eventRepository.saveAnswers(toAnswers(application.getId(), command));

        return new EventApplicationResult(application.getId(), event);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorSliceResult<EventApplicationSummary> getMyApplications(
        Long memberId,
        EventApplicationCursor cursor,
        int size
    ) {
        // 다음 페이지가 있는지 알려면 한 건 더 읽어봐야 한다
        List<EventApplication> fetched = eventRepository.findApplicationSlice(memberId, cursor, size + 1);

        // 행사는 한 번에 모아 조회한다. 신청마다 따로 읽으면 페이지 크기만큼 쿼리가 더 나간다
        Map<Long, Event> events = eventRepository.findAllByIds(
                fetched.stream().limit(size).map(EventApplication::getEventId).distinct().toList())
            .stream()
            .collect(Collectors.toMap(Event::getId, Function.identity()));

        return CursorSliceResult.ofSlice(
            fetched,
            size,
            application -> EventApplicationSummary.of(application, events.get(application.getEventId())),
            summary -> EventApplicationCursor.of(summary).format()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public EventApplicationDetail getMyApplication(Long applicationId, Long memberId) {
        EventApplication application = getMyApplicationOrThrow(applicationId, memberId);
        Event event = eventRepository.findById(application.getEventId())
            .orElseThrow(() -> new BusinessException(EventErrorCode.EVENT_NOT_FOUND));

        // 질문은 행사 것을 쓰고 답변만 이 신청 것을 붙인다. 취소 후 재신청이면 신청마다 답변이 따로 있다
        return EventApplicationDetail.of(
            event,
            application,
            eventRepository.findQuestionsByEventId(event.getId()),
            eventRepository.findAnswersByApplicationId(applicationId)
        );
    }

    @Override
    @Transactional
    public void cancelApplication(Long applicationId, Long memberId) {
        EventApplication application = getMyApplicationOrThrow(applicationId, memberId);
        if (application.isCanceled()) {
            throw new BusinessException(EventErrorCode.ALREADY_CANCELED);
        }

        Event event = eventRepository.findById(application.getEventId())
            .orElseThrow(() -> new BusinessException(EventErrorCode.EVENT_NOT_FOUND));

        // 마감 판정과 취소 시각이 같은 시각을 봐야 마감 직전 취소가 마감 이후로 기록되지 않는다
        LocalDateTime now = LocalDateTime.now();
        // 취소는 신청 마감 전까지만 허용한다. 강제 마감·정원 마감은 이미 낸 신청의 취소를 막지 않는다
        if (event.isAfterApplyPeriod(now)) {
            throw new BusinessException(EventErrorCode.CANCEL_DEADLINE_PASSED);
        }

        application.cancel(now);
        eventRepository.saveApplication(application);
    }

    /** 남의 신청은 없는 것으로 보여야 하므로 조회 단계에서 회원 식별자로 함께 거른다. */
    private EventApplication getMyApplicationOrThrow(Long applicationId, Long memberId) {
        return eventRepository.findApplicationByIdAndMemberId(applicationId, memberId)
            .orElseThrow(() -> new BusinessException(EventErrorCode.APPLICATION_NOT_FOUND));
    }

    /**
     * 모집 중인 행사를 가져온다. 폼 조회와 신청이 같은 기준으로 열려 있어야 하므로 한곳에 둔다.
     * <p>
     * 아직 게시하지 않은 행사는 학생에게 없는 것으로 보여야 하므로 목록·상세와 같은 기준으로 거른다.
     * 닫혀 있으면 사유를 가른다. 강제 마감·기간 종료가 정원 마감보다 앞선 사유다.
     */
    private Event getOpenEvent(Long eventId) {
        Event event = eventRepository.findPublishedById(eventId)
            .orElseThrow(() -> new BusinessException(EventErrorCode.EVENT_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        long appliedCount = eventRepository.countAppliedByEventId(eventId);
        if (event.calculateRecruitStatus(now, appliedCount) != RecruitStatus.OPEN) {
            throw new BusinessException(event.isClosedByCapacityOnly(now, appliedCount)
                ? EventErrorCode.CAPACITY_FULL
                : EventErrorCode.ALREADY_CLOSED);
        }
        return event;
    }

    private List<EventApplicationAnswer> toAnswers(Long applicationId, EventApplyCommand command) {
        return command.answers().stream()
            .filter(answer -> !answer.isEmpty())
            .map(answer -> EventApplicationAnswer.create(
                applicationId,
                answer.questionId(),
                answer.answerText(),
                answer.selectedOptions() == null ? List.of() : answer.selectedOptions()
            ))
            .toList();
    }
}
