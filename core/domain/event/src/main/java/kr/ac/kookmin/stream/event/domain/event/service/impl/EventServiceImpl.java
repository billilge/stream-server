package kr.ac.kookmin.stream.event.domain.event.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventSummary;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.event.domain.event.repository.EventApplicationRepository;
import kr.ac.kookmin.stream.event.domain.event.repository.EventRepository;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class EventServiceImpl implements EventService {

    // 전체 목록에서 모집 상태 구간을 보여주는 순서
    private static final List<RecruitStatus> LIST_ORDER =
        List.of(RecruitStatus.OPEN, RecruitStatus.BEFORE_OPEN, RecruitStatus.CLOSED);

    private final EventRepository eventRepository;
    private final EventApplicationRepository eventApplicationRepository;

    // 행사 목록과 신청자 수를 따로 읽어 정원 판정에 함께 쓰므로 한 스냅샷으로 묶는다
    @Override
    @Transactional(readOnly = true)
    public List<Event> getApplicableEvents(Collection<Long> eventIds) {
        List<Event> events = eventRepository.findPublishedByIds(eventIds);

        // 신청자 수는 한 번에 모아 읽는다. 행사마다 따로 세면 개수만큼 쿼리가 더 나간다
        Map<Long, Long> applicantCounts = eventApplicationRepository.countAppliedByEventIds(
            events.stream().map(Event::getId).toList());

        // 모집 상태 판정은 목록·상세·신청과 같은 기준을 써야 하므로 Event의 계산을 그대로 쓴다
        LocalDateTime now = LocalDateTime.now();
        return events.stream()
            .filter(event -> event.calculateRecruitStatus(
                now, applicantCounts.getOrDefault(event.getId(), 0L)) == RecruitStatus.OPEN)
            .sorted(Comparator.comparing(Event::getApplyEndAt).thenComparing(Event::getId))
            .toList();
    }

    @Override
    public List<Event> getPublishedByIds(Collection<Long> eventIds) {
        return eventRepository.findPublishedByIds(eventIds);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorSliceResult<EventSummary> getPublishedEvents(
        RecruitStatus recruitStatus,
        EventCursor cursor,
        int size
    ) {
        // 필터링(DB)과 응답(도메인)이 같은 시각을 봐야 모집 상태가 어긋나지 않는다
        LocalDateTime now = LocalDateTime.now();
        // 다음 페이지가 있는지 알려면 한 건 더 읽어봐야 한다.
        // 모집 상태마다 정렬 기준이 달라 구간별로 읽고, 앞 구간이 모자라면 다음 구간으로 채운다
        List<Event> fetched = new ArrayList<>();
        for (RecruitStatus status : statusesToRead(recruitStatus, cursor)) {
            EventCursor statusCursor = cursor != null && cursor.recruitStatus() == status ? cursor : null;
            fetched.addAll(eventRepository.findPublishedSlice(status, statusCursor, size + 1 - fetched.size(), now));
            if (fetched.size() > size) {
                break;
            }
        }

        // 신청자 수는 한 번에 모아 조회한다. 행사마다 따로 세면 페이지 크기만큼 쿼리가 더 나간다
        Map<Long, Long> applicantCounts = eventApplicationRepository.countAppliedByEventIds(
            fetched.stream().limit(size).map(Event::getId).toList());

        // 다음 커서의 구간은 응답에 실린 모집 상태로 정한다. DB 판정과 도메인 판정이 같아야 구간이 맞게 이어진다
        return CursorSliceResult.ofSlice(
            fetched,
            size,
            event -> EventSummary.of(event, applicantCounts.getOrDefault(event.getId(), 0L), now),
            summary -> EventCursor.of(summary).format()
        );
    }

    // 필터가 있으면 그 상태만, 없으면 전체 순서대로 읽는다. 커서가 가리키는 구간보다 앞은 이미 다 읽었으니 건너뛴다
    private static List<RecruitStatus> statusesToRead(RecruitStatus recruitStatus, EventCursor cursor) {
        List<RecruitStatus> statuses = recruitStatus == null ? LIST_ORDER : List.of(recruitStatus);
        if (cursor == null) {
            return statuses;
        }
        int from = statuses.indexOf(cursor.recruitStatus());
        // 필터와 상태가 다른 커서는 다른 목록에서 받은 것이다
        if (from < 0) {
            throw new BusinessException(EventErrorCode.EVENT_INVALID_CURSOR);
        }
        return statuses.subList(from, statuses.size());
    }

    @Override
    @Transactional(readOnly = true)
    public EventDetail getPublishedEvent(Long eventId) {
        Event event = eventRepository.findPublishedById(eventId)
            .orElseThrow(() -> new BusinessException(EventErrorCode.EVENT_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        return EventDetail.of(event, eventApplicationRepository.countAppliedByEventId(eventId), now);
    }

    @Override
    public Event getByIdOrNull(Long eventId) {
        return eventRepository.findById(eventId).orElse(null);
    }
}
