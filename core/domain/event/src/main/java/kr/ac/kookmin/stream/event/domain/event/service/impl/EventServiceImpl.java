package kr.ac.kookmin.stream.event.domain.event.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationForm;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplyCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.event.domain.event.repository.EventRepository;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventApplyAnswerValidator eventApplyAnswerValidator;

    @Override
    @Transactional(readOnly = true)
    public EventApplicationForm getApplicationForm(Long eventId) {
        Event event = getOpenEvent(eventId);
        return EventApplicationForm.of(event, eventRepository.findQuestionsByEventId(eventId));
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

        EventApplication application = eventRepository.saveApplication(EventApplication.of(
            null, eventId, memberId, EventApplicationStatus.APPLIED, LocalDateTime.now(), null
        ));
        eventRepository.saveAnswers(toAnswers(application.getId(), command));

        return EventApplicationResult.of(application.getId(), event);
    }

    /**
     * 모집 중인 행사를 가져온다. 폼 조회와 신청이 같은 기준으로 열려 있어야 하므로 한곳에 둔다.
     * <p>
     * 정원 마감은 기간 마감과 사유를 구분해야 해서 모집 상태 계산보다 먼저 본다.
     */
    private Event getOpenEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new BusinessException(EventErrorCode.EVENT_NOT_FOUND));

        long appliedCount = eventRepository.countAppliedByEventId(eventId);
        if (event.isCapacityFull(appliedCount)) {
            throw new BusinessException(EventErrorCode.CAPACITY_FULL);
        }
        if (event.calculateRecruitStatus(LocalDateTime.now(), appliedCount) != RecruitStatus.OPEN) {
            throw new BusinessException(EventErrorCode.ALREADY_CLOSED);
        }
        return event;
    }

    private List<EventApplicationAnswer> toAnswers(Long applicationId, EventApplyCommand command) {
        return command.answers().stream()
            .filter(answer -> !answer.isEmpty())
            .map(answer -> EventApplicationAnswer.of(
                null,
                applicationId,
                answer.questionId(),
                answer.answerText(),
                answer.selectedOptions() == null ? List.of() : answer.selectedOptions()
            ))
            .toList();
    }
}
