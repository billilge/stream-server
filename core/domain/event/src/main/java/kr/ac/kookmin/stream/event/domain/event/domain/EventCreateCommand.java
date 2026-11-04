package kr.ac.kookmin.stream.event.domain.event.domain;

import java.util.List;

public record EventCreateCommand(
    String title,
    String description,
    String place,
    EventPeriod eventPeriod,
    EventPeriod applyPeriod,
    RecruitPolicy recruitPolicy,
    List<Long> imageIds,
    List<EventQuestionCommand> questions
) {
}
