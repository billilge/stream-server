package kr.ac.kookmin.stream.welfare.domain.feedback.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRound;

public interface FeedbackRoundRepository {

    Optional<FeedbackRound> findById(Long id);

    // now 시각 기준으로 접수 기간(opens_at~closes_at) 안에 있는 회차 하나를 찾는다. 여러 회차 기간이 안 겹친다는 전제.
    Optional<FeedbackRound> findOpenAt(LocalDateTime now);

    /**
     * 답변을 아직 공개하지 않은 회차 중 가장 최근 것(연도·회차 내림차순).
     * 관리자 화면이 말하는 "진행 중 회차"(SCHEDULED·OPEN·ANSWERING)가 이 조건과 같다 —
     * 접수가 끝나 답변을 쓰는 중인 회차도 포함해야 답변 작성 화면이 질문을 받아올 수 있다.
     */
    Optional<FeedbackRound> findLatestUnpublished();

    // year가 null이면 전체 연도. 최신 회차부터(연도 내림차순 → 회차 내림차순) 정렬한다
    List<FeedbackRound> findAllByYear(Integer year);

    // 접수 기간이 주어진 구간과 하루라도 겹치는 회차가 있는지
    boolean existsOverlapping(LocalDateTime opensAt, LocalDateTime closesAt);

    // 그 연도의 가장 큰 회차 번호. 회차가 없으면 비어 있다
    Optional<Integer> findMaxRoundOf(int year);

    // 회차가 하나라도 존재했던 연도 목록(최신순) — 목록 화면 연도 드롭다운용
    List<Integer> findDistinctYears();

    // 해당 연도에 실제로 존재하는 회차 번호(오름차순) — 목록 화면 회차 칩용
    List<Integer> findRoundsByYear(int year);

    FeedbackRound save(FeedbackRound feedbackRound);
}
