package kr.ac.kookmin.stream.event;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationResult;

public record EventApplyResponse(
    Long applicationId,
    String title,
    LocalDateTime eventStartAt,
    String place
) {

    public static EventApplyResponse from(EventApplicationResult result) {
        Event event = result.getEvent();
        return new EventApplyResponse(
            result.getApplicationId(),
            event.getTitle(),
            event.getEventStartAt(),
            event.getPlace()
        );
    }
}
