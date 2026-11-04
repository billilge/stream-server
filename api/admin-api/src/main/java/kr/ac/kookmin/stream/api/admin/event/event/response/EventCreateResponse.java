package kr.ac.kookmin.stream.api.admin.event.event.response;

import kr.ac.kookmin.stream.event.domain.event.domain.Event;

public record EventCreateResponse(Long eventId) {

    public static EventCreateResponse from(Event event) {
        return new EventCreateResponse(event.getId());
    }
}
