package kr.ac.kookmin.stream.api.ai.welfare.feedback.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;

/**
 * 챗봇 인덱싱용 열린피드백. 질문과 답변 글만 담는다.
 * <p>
 * <b>질문한 학생({@code createdBy})과 답변한 운영진({@code answeredBy})은 절대 넣지 않는다.</b>
 * 열린피드백은 익명으로 받는다. 인덱스에 들어가면 챗봇 답변으로 새어 나갈 수 있다.
 */
public record AiFeedbackResponse(
    Long feedbackId,
    String question,
    String answer,
    LocalDateTime answeredAt
) {

    public static AiFeedbackResponse from(Feedback feedback) {
        return new AiFeedbackResponse(
            feedback.getId(),
            feedback.getQuestion(),
            feedback.getAnswer(),
            feedback.getAnsweredAt()
        );
    }
}
