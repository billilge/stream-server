package kr.ac.kookmin.stream.event.domain.event.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum QuestionType {

    SHORT_TEXT(50),
    LONG_TEXT(500),
    SINGLE_CHOICE(null),
    MULTIPLE_CHOICE(null);

    /** 답변 길이 제한. 선택형 질문은 답변이 텍스트가 아니라 null이다. */
    private final Integer maxLength;
}
