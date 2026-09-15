package kr.ac.kookmin.stream.db.event;

/**
 * 행사별 유효 신청자 수 집계 결과. JPQL 생성자 표현식으로 채워지는 조회 전용 프로젝션이다.
 */
public record EventApplicantCountProjection(Long eventId, Long applicantCount) {}
