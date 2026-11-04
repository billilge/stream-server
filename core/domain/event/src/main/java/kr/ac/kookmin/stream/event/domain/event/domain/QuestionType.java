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

    /** 선택지에서 고르는 질문인지. 선택지·'기타' 선택지는 이 유형에만 의미가 있다. */
    public boolean isChoice() {
        return this == SINGLE_CHOICE || this == MULTIPLE_CHOICE;
    }
}
