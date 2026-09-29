package kr.ac.kookmin.stream.welfare.domain.feedback.repository;

import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.OpenFeedback;

public interface OpenFeedbackRepository {

    Optional<OpenFeedback> findById(Long id);

    // year/round가 null이면 그 조건은 필터하지 않는다
    PageResult<OpenFeedback> search(Integer year, Integer round, PageOffset pageOffset);

    OpenFeedback save(OpenFeedback feedback);
}
