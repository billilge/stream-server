package kr.ac.kookmin.stream.api.admin.event.event.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record EventSearchParams(
    @Schema(description = "행사 제목 검색어", example = "체육대회")
    String keyword
) {
}
