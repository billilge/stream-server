package kr.ac.kookmin.stream.api.ai.event.event.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;

/**
 * 챗봇 인덱싱용 행사. "언제·어디서·무엇을"에 답할 글만 담는다.
 * 모집 인원·신청자 수처럼 계속 바뀌는 값은 인덱싱하면 틀린 답이 나가므로 넣지 않는다.
 * {@code applyEndAt}은 챗봇이 마감된 행사를 걸러내는 유효기간으로 쓴다.
 */
public record AiEventResponse(
    Long eventId,
    String title,
    String description,
    String place,
    LocalDateTime applyStartAt,
    LocalDateTime applyEndAt,
    LocalDateTime eventStartAt,
    LocalDateTime eventEndAt,
    LocalDateTime updatedAt
) {

    public static AiEventResponse from(Event event) {
        return new AiEventResponse(
            event.getId(),
            event.getTitle(),
            event.getDescription(),
            event.getPlace(),
            event.getApplyStartAt(),
            event.getApplyEndAt(),
            event.getEventStartAt(),
            event.getEventEndAt(),
            event.getUpdatedAt()
        );
    }
}
