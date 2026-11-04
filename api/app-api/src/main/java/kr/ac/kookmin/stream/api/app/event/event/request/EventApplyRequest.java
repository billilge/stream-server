package kr.ac.kookmin.stream.api.app.event.event.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplyCommand;

public record EventApplyRequest(
    @NotNull(message = "신청서 답변 목록을 입력해 주세요.")
    @Valid
    List<@NotNull(message = "답변 항목이 비어 있습니다.") AnswerRequest> answers
) {

    public EventApplyCommand toCommand() {
        return new EventApplyCommand(answers.stream().map(AnswerRequest::toCommand).toList());
    }

    /**
     * @param answerText      단답형·장문형 답변. 선택형 질문에서는 '기타'에 쓴 문구로, 질문이 '기타'를 허용할 때만 보낼 수 있다.
     *                        단일 선택은 선택지 하나와 '기타' 문구 중 하나만, 복수 선택은 둘을 함께 보낼 수 있다
     * @param selectedOptions 선택형 질문에서 고른 선택지의 0-based 인덱스
     */
    public record AnswerRequest(
        @NotNull(message = "답변 대상 질문을 입력해 주세요.")
        Long questionId,
        String answerText,
        List<Integer> selectedOptions
    ) {

        public EventApplyCommand.AnswerCommand toCommand() {
            return new EventApplyCommand.AnswerCommand(questionId, answerText, selectedOptions);
        }
    }
}
