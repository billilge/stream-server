package kr.ac.kookmin.stream.event.domain.event.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 행사 등록·수정 요청이 공통으로 갖는 행사 내용. 두 요청을 같은 규칙으로 검증하려고 둔다.
 * {@link EventCreateCommand}와 {@link EventUpdateCommand}가 구현하며, record 컴포넌트가 그대로 이 메서드들이 된다.
 */
public interface EventWriteCommand {

    LocalDateTime eventStartAt();

    LocalDateTime eventEndAt();

    LocalDateTime applyStartAt();

    LocalDateTime applyEndAt();

    RecruitType recruitType();

    /** 선착순 모집의 정원. 상시 모집이면 쓰이지 않는다 */
    Integer capacity();

    List<EventQuestionCommand> questions();
}
