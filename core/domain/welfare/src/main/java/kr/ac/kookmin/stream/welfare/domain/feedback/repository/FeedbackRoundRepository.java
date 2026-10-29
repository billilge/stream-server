package kr.ac.kookmin.stream.welfare.domain.feedback.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRound;

public interface FeedbackRoundRepository {

    Optional<FeedbackRound> findById(Long id);

    // now 시각 기준으로 접수 기간(opens_at~closes_at) 안에 있는 회차 하나를 찾는다. 여러 회차 기간이 안 겹친다는 전제.
    Optional<FeedbackRound> findOpenAt(LocalDateTime now);

    // 답변을 아직 공개하지 않은 회차 중 now까지 접수가 시작된(OPEN·ANSWERING) 회차에서 시작일이 가장 늦은 것
    Optional<FeedbackRound> findLatestStartedUnpublished(LocalDateTime now);

    // 답변을 아직 공개하지 않은 회차 중 now 이후에 접수가 시작되는(SCHEDULED) 회차에서 시작일이 가장 이른 것
    Optional<FeedbackRound> findNearestUpcoming(LocalDateTime now);

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
