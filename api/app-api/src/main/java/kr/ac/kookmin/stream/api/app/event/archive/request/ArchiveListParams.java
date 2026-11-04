package kr.ac.kookmin.stream.api.app.event.archive.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * @param year    조회할 활동 연도. 없으면 전체를 조회한다
 * @param keyword 제목 검색어. 없거나 공백뿐이면 검색하지 않는다
 */
public record ArchiveListParams(
    // LocalDate로 바꿔 범위 조건을 만들므로 표현 가능한 연도로 제한한다. 없는 연도는 에러가 아니라 빈 목록이다
    @Min(value = 2000, message = "연도는 2000 이상 2100 이하여야 합니다.")
    @Max(value = 2100, message = "연도는 2000 이상 2100 이하여야 합니다.")
    Integer year,

    @Size(max = 50, message = "검색어는 50자 이하로 입력해 주세요.")
    String keyword
) {
}
