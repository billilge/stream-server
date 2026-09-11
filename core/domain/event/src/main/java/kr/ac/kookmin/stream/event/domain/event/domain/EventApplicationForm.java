package kr.ac.kookmin.stream.event.domain.event.domain;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 신청서 폼 조회 결과. 행사 요약과 질문 목록을 함께 돌려주기 위한 읽기 모델이다.
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EventApplicationForm {

    private Event event;
    private List<EventQuestion> questions;

    public static EventApplicationForm of(Event event, List<EventQuestion> questions) {
        return new EventApplicationForm(event, questions);
    }
}
