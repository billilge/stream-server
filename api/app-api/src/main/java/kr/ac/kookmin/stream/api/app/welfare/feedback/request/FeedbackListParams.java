package kr.ac.kookmin.stream.api.app.welfare.feedback.request;

import jakarta.validation.constraints.Size;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackPeriod;

/**
 * @param year    조회할 연도. 없으면 전체 연도를 조회한다
 * @param round   조회할 회차. 없으면 그 연도의 전체 회차를 조회한다
 * @param keyword 질문 검색어. 없거나 공백뿐이면 검색하지 않는다
 */
public record FeedbackListParams(
    Integer year,

    Integer round,

    @Size(max = 50, message = "검색어는 50자 이하로 입력해 주세요.")
    String keyword
) {

    public FeedbackPeriod toPeriod() {
        return FeedbackPeriod.of(year, round);
    }
}
