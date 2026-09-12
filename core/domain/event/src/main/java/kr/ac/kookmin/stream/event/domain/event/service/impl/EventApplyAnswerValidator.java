package kr.ac.kookmin.stream.event.domain.event.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplyCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.QuestionType;
import org.springframework.stereotype.Component;

/**
 * 신청서 답변이 질문 구성에 맞는지 검증한다.
 * <p>
 * 같은 answerText라도 질문 유형에 따라 길이 한도가 달라 Bean Validation으로는 처리할 수 없다.
 * 질문을 조회한 뒤에야 판정할 수 있는 규칙을 모아 둔다.
 */
@Component
class EventApplyAnswerValidator {

    void validate(List<EventQuestion> questions, EventApplyCommand command) {
        Map<Long, EventQuestion> questionById = questions.stream()
            .collect(Collectors.toMap(EventQuestion::getId, Function.identity()));

        Set<Long> answeredQuestionIds = new HashSet<>();
        for (EventApplyCommand.AnswerCommand answer : command.answers()) {
            EventQuestion question = questionById.get(answer.questionId());
            if (question == null) {
                throw new BusinessException(EventErrorCode.INVALID_ANSWER);
            }
            if (!answeredQuestionIds.add(answer.questionId())) {
                throw new BusinessException(EventErrorCode.INVALID_ANSWER);
            }
            if (answer.isEmpty()) {
                // 선택 질문은 생략과 빈 답변을 같게 본다.
                // 이미 답변한 질문으로 기록됐으므로 아래 누락 검사가 못 잡는다. 필수면 여기서 걸러야 한다
                if (question.isRequired()) {
                    throw new BusinessException(EventErrorCode.INVALID_ANSWER);
                }
                continue;
            }
            validateAnswer(question, answer);
        }

        boolean requiredMissing = questions.stream()
            .anyMatch(question -> question.isRequired() && !answeredQuestionIds.contains(question.getId()));
        if (requiredMissing) {
            throw new BusinessException(EventErrorCode.INVALID_ANSWER);
        }
    }

    private void validateAnswer(EventQuestion question, EventApplyCommand.AnswerCommand answer) {
        String answerText = answer.answerText();
        List<Integer> selectedOptions = answer.selectedOptions() == null ? List.of() : answer.selectedOptions();
        QuestionType questionType = question.getQuestionType();
        // 선택형은 answerText를 쓰지 않는다. 미사용 필드를 빈 문자열로 채워 보내는 클라이언트도 있어
        // AnswerCommand.isEmpty()와 같은 기준(null 또는 blank)으로 "값 없음"을 판정한다
        boolean hasAnswerText = answerText != null && !answerText.isBlank();

        switch (questionType) {
            case SHORT_TEXT, LONG_TEXT -> {
                if (!hasAnswerText || !selectedOptions.isEmpty()) {
                    throw new BusinessException(EventErrorCode.INVALID_ANSWER);
                }
                if (answerText.length() > questionType.maxLength()) {
                    throw new BusinessException(EventErrorCode.INVALID_ANSWER);
                }
            }
            case SINGLE_CHOICE -> {
                if (hasAnswerText || selectedOptions.size() != 1) {
                    throw new BusinessException(EventErrorCode.INVALID_ANSWER);
                }
                validateSelectedOptionRange(question, selectedOptions);
            }
            case MULTIPLE_CHOICE -> {
                if (hasAnswerText || selectedOptions.isEmpty()) {
                    throw new BusinessException(EventErrorCode.INVALID_ANSWER);
                }
                if (selectedOptions.stream().distinct().count() != selectedOptions.size()) {
                    throw new BusinessException(EventErrorCode.INVALID_ANSWER);
                }
                validateSelectedOptionRange(question, selectedOptions);
            }
        }
    }

    /** selectedOptions는 질문 options 배열의 0-based 인덱스다. */
    private void validateSelectedOptionRange(EventQuestion question, List<Integer> selectedOptions) {
        int optionSize = question.getOptions() == null ? 0 : question.getOptions().size();
        boolean outOfRange = selectedOptions.stream()
            .anyMatch(index -> index == null || index < 0 || index >= optionSize);
        if (outOfRange) {
            throw new BusinessException(EventErrorCode.INVALID_ANSWER);
        }
    }
}
