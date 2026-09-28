package kr.ac.kookmin.stream.welfare.domain.feedback.service;

import java.util.List;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackPeriod;

public interface FeedbackService {

    Feedback getById(Long id);

    PageResult<Feedback> search(FeedbackPeriod period, PageOffset pageOffset);

    // 회차는 현재 열려 있는 회차로 서버가 자동 배정한다
    Feedback create(Long memberId, String question);

    // 회차가 하나라도 존재했던 연도 목록(최신순)
    List<Integer> getAvailableYears();

    // 해당 연도에 실제로 존재하는 회차 번호(오름차순)
    List<Integer> getRoundsOf(int year);
}
