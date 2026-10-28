package kr.ac.kookmin.stream.event.domain.event.domain;

import java.util.List;

/**
 * 행사 등록·수정 요청이 공통으로 갖는 행사 내용. 두 요청을 같은 규칙으로 검증하려고 둔다.
 * {@link EventCreateCommand}와 {@link EventUpdateCommand}가 구현하며, record 컴포넌트가 그대로 이 메서드들이 된다.
 * 기간 순서와 모집 정원은 {@link EventPeriod}·{@link RecruitPolicy}가 만들어질 때 검증하므로 여기에 두지 않는다.
 */
public interface EventWriteCommand {

    List<EventQuestionCommand> questions();
}
