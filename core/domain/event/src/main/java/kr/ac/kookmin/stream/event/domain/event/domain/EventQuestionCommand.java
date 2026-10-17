package kr.ac.kookmin.stream.event.domain.event.domain;

import java.util.List;

/**
 * 행사 등록·수정 요청에 담긴 신청서 질문 한 건. 노출 순서는 목록에서의 위치로 정한다.
 */
public record EventQuestionCommand(
    String questionText,
    QuestionType questionType,
    boolean required,
    List<String> options,
    boolean otherOptionAllowed
) {
}
