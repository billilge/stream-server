package kr.ac.kookmin.stream.api.ai.welfare.feedback.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;

/**
 * 챗봇 인덱싱용 열린피드백. 질문과 답변 글, 그리고 어느 회차의 답변인지만 담는다.
 * <p>
 * 학생 앱이 열린피드백을 회차 단위로 보여주므로({@code FeedbackPeriod}), 챗봇도 "2026년 2회차 답변"처럼
 * 같은 단위로 출처를 말할 수 있게 연도·회차를 함께 내려준다. 답변일만으로는 회차를 짐작해야 한다.
 * <p>
 * <b>질문한 학생({@code createdBy})과 답변한 운영진({@code answeredBy})은 절대 넣지 않는다.</b>
 * 열린피드백은 익명으로 받는다. 인덱스에 들어가면 챗봇 답변으로 새어 나갈 수 있다.
 */
public record AiFeedbackResponse(
    Long feedbackId,
    int year,
    int round,
    String question,
    String answer,
    LocalDateTime answeredAt
) {

    public static AiFeedbackResponse from(Feedback feedback) {
        return new AiFeedbackResponse(
            feedback.getId(),
            feedback.getYear(),
            feedback.getRound(),
            feedback.getQuestion(),
            feedback.getAnswer(),
            feedback.getAnsweredAt()
        );
    }
}
