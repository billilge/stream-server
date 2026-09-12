package kr.ac.kookmin.stream.event.domain.event.domain;

import java.util.List;

/**
 * 신청서 폼 조회 결과. 행사 요약과 질문 목록을 함께 돌려주기 위한 읽기 모델이다.
 */
public record EventApplicationForm(Event event, List<EventQuestion> questions) {
}
