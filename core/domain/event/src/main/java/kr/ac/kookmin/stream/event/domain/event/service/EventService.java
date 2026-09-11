package kr.ac.kookmin.stream.event.domain.event.service;

import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationForm;

public interface EventService {

    EventApplicationForm getApplicationForm(Long eventId);
}
