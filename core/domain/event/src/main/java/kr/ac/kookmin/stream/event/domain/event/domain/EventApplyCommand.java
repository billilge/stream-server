package kr.ac.kookmin.stream.event.domain.event.domain;

import java.util.List;

/**
 * 행사 신청 요청. 질문별 답변 목록을 담는다.
 */
public record EventApplyCommand(List<AnswerCommand> answers) {

    /**
     * 질문 하나에 대한 답변.
     *
     * @param answerText      단답형·장문형 답변. 선택형이면 null
     * @param selectedOptions 선택형 질문에서 고른 선택지의 0-based 인덱스. 주관식이면 빈 목록
     */
    public record AnswerCommand(Long questionId, String answerText, List<Integer> selectedOptions) {

        /**
         * 답변 내용이 비어 있는지. 선택 질문은 생략과 빈 답변을 같게 취급하므로 판정 기준을 한곳에 둔다.
         */
        public boolean isEmpty() {
            return (answerText == null || answerText.isBlank())
                && (selectedOptions == null || selectedOptions.isEmpty());
        }
    }
}
