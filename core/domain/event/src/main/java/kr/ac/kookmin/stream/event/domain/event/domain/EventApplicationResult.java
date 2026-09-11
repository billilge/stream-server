package kr.ac.kookmin.stream.event.domain.event.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 행사 신청 결과. 생성된 신청 식별자와 행사 요약을 함께 돌려주기 위한 읽기 모델이다.
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EventApplicationResult {

    private Long applicationId;
    private Event event;

    public static EventApplicationResult of(Long applicationId, Event event) {
        return new EventApplicationResult(applicationId, event);
    }
}
