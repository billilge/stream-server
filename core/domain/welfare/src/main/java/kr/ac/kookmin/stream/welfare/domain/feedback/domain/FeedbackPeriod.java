package kr.ac.kookmin.stream.welfare.domain.feedback.domain;

import kr.ac.kookmin.stream.common.BusinessException;

/**
 * 피드백 목록 조회의 연도·회차 필터. round의 유효성(1 이상)은 "검색"이라는 맥락과는 무관한,
 * round 값 자체의 불변식이라 이 VO의 정적 팩토리에서 검증한다(검색 로직과 관심사를 분리).
 *
 * @param year  null이면 전체 연도
 * @param round null이면 그 연도의 전체 회차
 */
public record FeedbackSearchCondition(Integer year, Integer round) {

    public static FeedbackSearchCondition of(Integer year, Integer round) {
        if (round != null && round <= 0) {
            throw new BusinessException(FeedbackErrorCode.INVALID_FEEDBACK_ROUND);
        }
        return new FeedbackSearchCondition(year, round);
    }
}
