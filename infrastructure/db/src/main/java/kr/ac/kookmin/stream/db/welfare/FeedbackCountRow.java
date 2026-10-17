package kr.ac.kookmin.stream.db.welfare;

// 회차별 집계 쿼리의 결과 한 줄. 도메인 레코드(FeedbackCount)로는 RepositoryImpl이 옮긴다
public interface FeedbackCountRow {

    int getYear();

    int getRound();

    long getQuestionCount();

    long getAnswerCount();
}
