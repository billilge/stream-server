package kr.ac.kookmin.stream.common;

import java.util.ArrayList;
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

    // 커서를 끝까지 넘기며 content를 모두 모은다 (PageResult.fetchAll의 커서 버전. 챗봇 인덱싱처럼 목록 전체가 필요할 때)
    // 첫 페이지는 커서 없이(null) 부르고, 다음 페이지부터는 응답의 nextCursor 문자열을 도메인 커서로 바꿔 넘긴다
    public static <C, T> List<T> fetchAll(
        Function<String, C> parseCursor,
        Function<C, CursorSliceResult<T>> fetchSlice
    ) {
        List<T> all = new ArrayList<>();
        C cursor = null;
        do {
            CursorSliceResult<T> slice = fetchSlice.apply(cursor);
            all.addAll(slice.content());
            cursor = slice.hasNext() ? parseCursor.apply(slice.nextCursor()) : null;
        } while (cursor != null);
        return all;
    }
}
