package kr.ac.kookmin.stream.welfare.domain.feedback.service;

import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundOptions;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackSearchCondition;

public interface FeedbackService {

    Feedback getById(Long id);

    PageResult<Feedback> search(FeedbackSearchCondition condition, PageOffset pageOffset);

    // 회차는 현재 열려 있는 회차로 서버가 자동 배정한다
    Feedback create(Long memberId, String question);

    // year가 null이면 현재 연도 기준
    FeedbackRoundOptions getRoundOptions(Integer year);
}
