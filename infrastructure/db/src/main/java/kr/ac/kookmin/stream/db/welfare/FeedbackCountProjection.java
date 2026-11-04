package kr.ac.kookmin.stream.db.welfare;

/**
 * 회차별 질문 수·답변 수 집계 결과. JPQL 생성자 표현식으로 채워지는 조회 전용 프로젝션이다.
 * 도메인 레코드(FeedbackCount)로는 RepositoryImpl이 옮긴다.
 */
public record FeedbackCountProjection(int year, int round, long questionCount, long answerCount) {}
