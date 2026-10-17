package kr.ac.kookmin.stream.event.domain.event.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @param capacity 선착순 모집의 정원. 상시 모집이면 쓰이지 않는다
 */
public record EventCreateCommand(
    String title,
    String description,
    String place,
    LocalDateTime eventStartAt,
    LocalDateTime eventEndAt,
    LocalDateTime applyStartAt,
    LocalDateTime applyEndAt,
    RecruitType recruitType,
    Integer capacity,
    boolean feePaidOnly,
    List<Long> imageIds,
    List<EventQuestionCommand> questions
) {
}
