package kr.ac.kookmin.stream.api.admin.internal.home.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 신청 섹션 후보 목록 필터. {@code @ModelAttribute}로 받으므로 잘못된 값은 400으로 떨어진다 —
 * {@code @RequestParam}으로 받으면 변환 실패가 500이 된다.
 *
 * @param isExposed 생략하면 노출 여부로 거르지 않는다
 */
public record AdminHomeApplySectionSearchParams(

    @Schema(description = "노출 여부 필터. 생략하면 전체")
    Boolean isExposed
) {
}
