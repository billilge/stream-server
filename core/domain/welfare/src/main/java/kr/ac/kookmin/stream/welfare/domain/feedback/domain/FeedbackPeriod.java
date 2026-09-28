package kr.ac.kookmin.stream.welfare.domain.feedback.domain;

import kr.ac.kookmin.stream.common.BusinessException;

/**
 * 연도·회차로 특정 회차를 가리키는 값. 실제로 존재하는 회차 엔티티({@link FeedbackRound})와 달리,
 * 존재 여부와 무관하게 "어느 회차를 가리키는가"만 표현한다 — 둘 다 없으면 "지정 없음"을 뜻한다.
 * round의 유효성(1 이상)은 이 값 자체의 불변식이라 여기서 검증한다.
 */
public record FeedbackPeriod(Integer year, Integer round) {

    public static FeedbackPeriod of(Integer year, Integer round) {
        if (round != null && round <= 0) {
            throw new BusinessException(FeedbackErrorCode.INVALID_FEEDBACK_ROUND);
        }
        return new FeedbackPeriod(year, round);
    }
}
