package kr.ac.kookmin.stream.api.common.dto;

import java.util.List;
import java.util.function.Function;
import kr.ac.kookmin.stream.common.PageResult;

public record PageResponse<T>(List<T> content, int page, int size, long totalCount, int totalPage) {

    public static <E, T> PageResponse<T> from(PageResult<E> result, Function<E, T> mapper) {
        return new PageResponse<>(
                result.content().stream().map(mapper).toList(),
                result.page(),
                result.size(),
                result.totalCount(),
                result.totalPage()
        );
    }
}
