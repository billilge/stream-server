package kr.ac.kookmin.stream;

import java.util.List;
import java.util.function.Function;
import kr.ac.kookmin.stream.common.CursorSliceResult;

public record CursorSliceResponse<T>(List<T> content, boolean hasNext, String nextCursor) {

    public static <E, T> CursorSliceResponse<T> from(CursorSliceResult<E> result, Function<E, T> mapper) {
        return new CursorSliceResponse<>(
                result.content().stream().map(mapper).toList(),
                result.hasNext(),
                result.nextCursor() == null ? null : CursorCodec.encode(result.nextCursor())
        );
    }
}