package kr.ac.kookmin.stream.event;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationForm;

public record EventFormResponse(
    Long eventId,
    String title,
    LocalDateTime eventStartAt,
    String place,
    List<EventFormQuestionResponse> questions
) {

    public static EventFormResponse from(EventApplicationForm form) {
        Event event = form.getEvent();
        return new EventFormResponse(
            event.getId(),
            event.getTitle(),
            event.getEventStartAt(),
            event.getPlace(),
            form.getQuestions().stream().map(EventFormQuestionResponse::from).toList()
        );
    }
}
