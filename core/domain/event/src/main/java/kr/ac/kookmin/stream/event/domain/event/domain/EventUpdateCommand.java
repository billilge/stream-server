package kr.ac.kookmin.stream.event.domain.event.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @param expectedUpdatedAt 수정 화면이 행사를 읽었을 때의 수정 시각. 그 사이 다른 운영진이 고쳤는지 확인한다
 */
public record EventUpdateCommand(
    String title,
    String description,
    String place,
    EventPeriod eventPeriod,
    EventPeriod applyPeriod,
    RecruitPolicy recruitPolicy,
    boolean feePaidOnly,
    List<Long> imageIds,
    List<EventQuestionCommand> questions,
    LocalDateTime expectedUpdatedAt
) implements EventWriteCommand {
}
