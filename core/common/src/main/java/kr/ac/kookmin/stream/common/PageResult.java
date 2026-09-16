package kr.ac.kookmin.stream.common;

import java.util.List;

public record PageResult<T>(List<T> content, int page, int size, long totalCount, int totalPage) {
}
