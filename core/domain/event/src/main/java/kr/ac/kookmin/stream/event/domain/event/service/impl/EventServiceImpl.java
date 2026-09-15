package kr.ac.kookmin.stream.event.domain.event.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationForm;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplyCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.EventSummary;
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
    public CursorSliceResult<EventSummary> getPublishedEvents(
        RecruitStatus recruitStatus,
        EventCursor cursor,
        int size
    ) {
        // 필터링(DB)과 응답(도메인)이 같은 시각을 봐야 모집 상태가 어긋나지 않는다
        LocalDateTime now = LocalDateTime.now();
        // 다음 페이지가 있는지 알려면 한 건 더 읽어봐야 한다
        List<Event> fetched = eventRepository.findPublishedSlice(recruitStatus, cursor, size + 1, now);

        // 신청자 수는 한 번에 모아 조회한다. 행사마다 따로 세면 페이지 크기만큼 쿼리가 더 나간다
        Map<Long, Long> applicantCounts = eventRepository.countAppliedByEventIds(
            fetched.stream().limit(size).map(Event::getId).toList());

        return CursorSliceResult.ofSlice(
            fetched,
            size,
            event -> EventSummary.of(event, applicantCounts.getOrDefault(event.getId(), 0L), now),
            summary -> EventCursor.of(summary).format()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public EventDetail getPublishedEvent(Long eventId) {
        Event event = eventRepository.findPublishedById(eventId)
            .orElseThrow(() -> new BusinessException(EventErrorCode.EVENT_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        return EventDetail.of(event, eventRepository.countAppliedByEventId(eventId), now);
    }

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
