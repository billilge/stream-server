package kr.ac.kookmin.stream.welfare.domain.feedback.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRound;

public interface FeedbackRoundRepository {

    // now 시각 기준으로 접수 기간(opens_at~closes_at) 안에 있는 회차 하나를 찾는다. 여러 회차 기간이 안 겹친다는 전제.
    Optional<FeedbackRound> findOpenAt(LocalDateTime now);

    // 회차가 하나라도 존재했던 연도 목록(최신순) — 목록 화면 연도 드롭다운용
    List<Integer> findDistinctYears();

    // 해당 연도에 실제로 존재하는 회차 번호(오름차순) — 목록 화면 회차 칩용
    List<Integer> findRoundsByYear(int year);
}
