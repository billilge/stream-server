package kr.ac.kookmin.stream.welfare.domain.feedback.domain;

// 답변 일괄 저장 요청 한 건. 답변자와 답변 시각은 서버가 채우므로 받지 않는다.
public record FeedbackAnswerCommand(Long feedbackId, String answer) {

    public static FeedbackAnswerCommand of(Long feedbackId, String answer) {
        return new FeedbackAnswerCommand(feedbackId, answer);
    }
}
