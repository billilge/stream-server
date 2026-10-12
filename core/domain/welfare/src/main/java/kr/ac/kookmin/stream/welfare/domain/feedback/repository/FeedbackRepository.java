package kr.ac.kookmin.stream.welfare.domain.feedback.repository;

import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;

public interface FeedbackRepository {

    Optional<Feedback> findById(Long id);

    // year/round가 null이면 그 조건은 필터하지 않는다
    PageResult<Feedback> search(Integer year, Integer round, PageOffset pageOffset);

    Feedback save(Feedback feedback);
}
