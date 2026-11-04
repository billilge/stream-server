package kr.ac.kookmin.stream.api.app.search.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @param keyword 검색어. 앞뒤 공백은 검색할 때 지운다
 */
public record SearchParams(
    @NotBlank(message = "검색어를 입력해 주세요.")
    @Size(max = 50, message = "검색어는 50자 이하로 입력해 주세요.")
    String keyword
) {
}
