package kr.ac.kookmin.stream.common;

import java.util.List;
import java.util.function.Function;

public record CursorSliceResult<T>(List<T> content, boolean hasNext, String nextCursor) {

    // size+1개 조회 결과에서 슬라이스를 조립한다. 마지막 한 건이 있으면 다음 페이지가 있다는 뜻이라 잘라내고,
    // 남은 마지막 항목으로 다음 커서를 만든다.
    public static <E, T> CursorSliceResult<T> ofSlice(
        List<E> fetched,
        int size,
        Function<E, T> mapper,
        Function<T, String> cursorFormatter
    ) {
        boolean hasNext = fetched.size() > size;
        List<T> content = fetched.stream().limit(size).map(mapper).toList();
        String nextCursor = hasNext ? cursorFormatter.apply(content.get(content.size() - 1)) : null;
        return new CursorSliceResult<>(content, hasNext, nextCursor);
    }
}
