package kr.ac.kookmin.stream.welfare.domain.chat.domain;

import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.common.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum ChatErrorCode implements ErrorCode {

    CHAT_FAQ_NOT_FOUND(ErrorStatus.NOT_FOUND, "존재하지 않는 추천 질문입니다."),
    CHAT_FAQ_LIMIT_EXCEEDED(ErrorStatus.BAD_REQUEST, "추천 질문은 5개까지 등록할 수 있습니다."),
    CHAT_FAQ_ADD_CONFLICT(ErrorStatus.CONFLICT, "다른 운영진이 동시에 질문을 추가해 저장하지 못했습니다. 새로고침한 뒤 다시 시도해 주세요.");

    private final int status;
    private final String message;
}
