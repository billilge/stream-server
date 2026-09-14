package kr.ac.kookmin.stream.event.domain.event.domain;

/**
 * 행사 신청 결과. 생성된 신청 식별자와 행사 요약을 함께 돌려주기 위한 읽기 모델이다.
 */
public record EventApplicationResult(Long applicationId, Event event) {
}
