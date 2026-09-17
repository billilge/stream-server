package kr.ac.kookmin.stream.common;

import java.util.List;
import java.util.function.Function;

public record PageResult<T>(List<T> content, int page, int size, long totalCount, int totalPage) {

    public static <T> PageResult<T> of(List<T> content, int page, int size, long totalCount) {
        return new PageResult<>(content, page, size, totalCount, totalPage(size, totalCount));
    }

    public static <T> PageResult<T> empty(int page, int size) {
        return new PageResult<>(List.of(), page, size, 0, 0);
    }

    // 페이지 메타는 그대로 두고 content만 다른 타입으로 옮긴다 (도메인 객체 → 응답 DTO)
    public <R> PageResult<R> map(Function<T, R> mapper) {
        return new PageResult<>(content.stream().map(mapper).toList(), page, size, totalCount, totalPage);
    }

    private static int totalPage(int size, long totalCount) {
        if (size <= 0) {
            return 0;
        }
        return (int) ((totalCount + size - 1) / size);
    }
}
