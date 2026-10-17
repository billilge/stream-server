package kr.ac.kookmin.stream.api.admin.event.event.response;

import kr.ac.kookmin.stream.event.domain.event.domain.Event;

public record EventUpdateResponse(Long eventId) {

    public static EventUpdateResponse from(Event event) {
        return new EventUpdateResponse(event.getId());
    }
}
