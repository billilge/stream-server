package kr.ac.kookmin.stream.event.domain.event.domain;

/**
 * 행사와 그 행사의 유효 신청자 수(status가 APPLIED인 신청). 모집 상태 계산에 필요해 함께 조회한다.
 */
public record EventApplicantCount(Event event, long applicantCount) {}
