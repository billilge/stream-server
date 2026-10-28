package kr.ac.kookmin.stream.event.domain.event.service.impl;

import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestionCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventWriteCommand;
import org.springframework.stereotype.Component;

/**
 * 운영진이 행사를 등록·수정할 때 요청 형식(Bean Validation)만으로는 걸러지지 않는 규칙을 확인한다.
 * 필드 하나로 판단할 수 없는 조합(질문 유형에 따른 선택지)을 본다. 기간 순서와 모집 방식에 따른 정원은
 * {@code EventPeriod}·{@code RecruitPolicy}를 만들 때 검증한다.
 */
@Component
class EventWriteValidator {

    void validate(EventWriteCommand command) {
        validateQuestions(command.questions());
    }

    private void validateQuestions(List<EventQuestionCommand> questions) {
        boolean choiceWithoutOptions = questions.stream()
            .anyMatch(question -> question.questionType().isChoice()
                && (question.options() == null || question.options().isEmpty()));
        if (choiceWithoutOptions) {
            throw new BusinessException(EventErrorCode.EVENT_INVALID_QUESTION);
        }
    }
}
