package kr.ac.kookmin.stream.welfare.domain.feedback.service;

import java.util.List;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackPeriod;

/**
 * 학생 앱의 열린피드백 질문 조회·작성. 운영진의 회차 관리와 답변 작성·공개는 {@link AdminFeedbackService}가 맡는다.
 */
public interface FeedbackService {

    // 답변이 공개된 질문이거나 본인 질문일 때만 돌려준다. 본인 질문이라도 답변을 공개하지 않은 회차면 답변을 비운다
    Feedback getById(Long id, Long memberId);

    // 답변을 공개한 회차의 답변 있는 질문만 돌려준다. 답변 전 질문은 작성자 본인만 내 작성내역에서 본다
    PageResult<Feedback> search(FeedbackPeriod period, PageOffset pageOffset);

    // 본인이 쓴 질문 전체(답변이 없는 질문도 포함). 답변을 공개하지 않은 회차의 질문은 답변을 비워서 돌려준다
    PageResult<Feedback> getMyFeedbacks(Long memberId, PageOffset pageOffset);

    // 회차는 현재 열려 있는 회차로 서버가 자동 배정한다
    Feedback create(Long memberId, String question);

    // 답변을 공개한 회차가 있는 연도 목록(최신순). 목록 조회와 같은 기준이다
    List<Integer> getAvailableYears();

    // 해당 연도에서 답변을 공개한 회차 번호(오름차순)
    List<Integer> getRoundsOf(int year);
}
