package kr.ac.kookmin.stream.event.domain.event.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCreateCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestionCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;
import org.springframework.stereotype.Component;

/**
 * 운영진이 행사를 등록·수정할 때 요청 형식(Bean Validation)만으로는 걸러지지 않는 규칙을 확인한다.
 * 필드 하나로 판단할 수 없는 조합(시작·종료 순서, 모집 방식에 따른 정원, 질문 유형에 따른 선택지)을 본다.
 */
@Component
class EventWriteValidator {

    void validate(EventCreateCommand command) {
        validatePeriod(command.eventStartAt(), command.eventEndAt());
        validatePeriod(command.applyStartAt(), command.applyEndAt());
        validateCapacity(command.recruitType(), command.capacity());
        validateQuestions(command.questions());
    }

    private void validatePeriod(LocalDateTime startAt, LocalDateTime endAt) {
        if (!endAt.isAfter(startAt)) {
            throw new BusinessException(EventErrorCode.EVENT_INVALID_PERIOD);
        }
    }

    // 상시 모집은 인원 제한이 없어 정원을 보지 않는다
    private void validateCapacity(RecruitType recruitType, Integer capacity) {
        if (recruitType == RecruitType.FIRST_COME && (capacity == null || capacity < 1)) {
            throw new BusinessException(EventErrorCode.EVENT_INVALID_CAPACITY);
        }
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
