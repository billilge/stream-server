package kr.ac.kookmin.stream.api.admin.event.event.response;

import kr.ac.kookmin.stream.event.domain.event.domain.Event;

public record EventPublishResponse(Long eventId, boolean isPublished) {

    public static EventPublishResponse from(Event event) {
        return new EventPublishResponse(event.getId(), event.isPublished());
    }
}
