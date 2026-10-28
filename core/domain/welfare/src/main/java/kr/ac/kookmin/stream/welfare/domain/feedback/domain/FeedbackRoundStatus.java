package kr.ac.kookmin.stream.welfare.domain.feedback.domain;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.common.BusinessException;

/**
 * 회차 상태. 컬럼으로 저장하지 않고 접수 기간과 답변 공개 여부로 그때그때 판정한다 —
 * 시각이 지나면 저절로 바뀌는 값이라 저장해두면 갱신 사이에 틀린 값이 남는다.
 */
public enum FeedbackRoundStatus {

    SCHEDULED,  // 오픈예정 — 접수 시작 전
    OPEN,       // 질문접수중
    ANSWERING,  // 답변작성중 — 접수는 끝났고 답변은 아직 비공개
    CLOSED;     // 회차종료 — 답변을 공개함

    public static FeedbackRoundStatus of(FeedbackRound round, LocalDateTime now) {
        if (round.isAnswersPublished()) {
            return CLOSED;
        }
        if (now.isBefore(round.getOpensAt())) {
            return SCHEDULED;
        }
        if (now.isAfter(round.getClosesAt())) {
            return ANSWERING;
        }
        return OPEN;
    }

    // Spring MVC는 잘못된 enum 문자열을 MethodArgumentNotValidException이 아닌 다른 예외로 던지므로
    // 컨트롤러가 String으로 받아 이 메서드로 변환한다. null(상태 필터 미지정)은 그대로 null을 돌려준다.
    public static FeedbackRoundStatus from(String value) {
        if (value == null) {
            return null;
        }
        try {
            return FeedbackRoundStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(FeedbackErrorCode.INVALID_FEEDBACK_ROUND_STATUS);
        }
    }
}
