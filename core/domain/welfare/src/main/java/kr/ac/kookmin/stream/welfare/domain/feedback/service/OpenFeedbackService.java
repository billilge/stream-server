package kr.ac.kookmin.stream.welfare.domain.feedback.service;

import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundOptions;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.OpenFeedback;

public interface OpenFeedbackService {

    OpenFeedback getById(Long id);

    // year/round가 null이면 그 조건은 전체(필터 없음)
    PageResult<OpenFeedback> search(Integer year, Integer round, PageOffset pageOffset);

    // 회차는 현재 열려 있는 회차로 서버가 자동 배정한다
    OpenFeedback create(Long memberId, String question);

    // year가 null이면 현재 연도 기준
    FeedbackRoundOptions getRoundOptions(Integer year);
}
