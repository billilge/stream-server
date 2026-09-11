package kr.ac.kookmin.stream.event.domain.event.service;

import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationForm;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplyCommand;

public interface EventService {

    EventApplicationForm getApplicationForm(Long eventId);

    EventApplicationResult apply(Long eventId, Long memberId, EventApplyCommand command);
}
