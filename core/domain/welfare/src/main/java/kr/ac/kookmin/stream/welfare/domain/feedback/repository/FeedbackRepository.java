package kr.ac.kookmin.stream.welfare.domain.feedback.repository;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackCount;

public interface FeedbackRepository {

    Optional<Feedback> findById(Long id);

    // year/round가 null이면 그 조건은 필터하지 않는다
    PageResult<Feedback> search(Integer year, Integer round, PageOffset pageOffset);

    // 한 회차의 질문 전체(답변이 없는 질문도 포함). 질문 시각 오름차순
    List<Feedback> findAllByYearAndRound(int year, int round);

    /**
     * 관리자 질문 검색. keyword는 질문 내용에, memberIds는 작성자에 걸고 둘을 OR로 묶는다.
     * 작성자 이름은 member 모듈에 있어 welfare가 조인할 수 없으므로, 이름으로 찾은 회원 ID를 받아
     * 자기 테이블의 created_by와만 비교한다.
     *
     * @param keyword   null이면 검색 조건 없이 그 회차 전체
     * @param memberIds 이름이 keyword와 일치한 회원 ID. 일치가 없으면 빈 목록
     */
    PageResult<Feedback> searchByKeyword(
        int year, int round, String keyword, List<Long> memberIds, PageOffset pageOffset);

    // 한 회원이 작성한 질문 수(연도·회차 무관)
    long countByMemberId(Long memberId);

    // 회차별 질문 수·답변 수 집계. year가 null이면 전체 연도. 질문이 없는 회차는 결과에 포함되지 않는다
    List<FeedbackCount> findCountsByYear(Integer year);

    Feedback save(Feedback feedback);

    List<Feedback> saveAll(List<Feedback> feedbacks);
}
