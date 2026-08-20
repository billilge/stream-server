package kr.ac.kookmin.stream;

import java.util.List;
import kr.ac.kookmin.stream.common.CursorSliceResult;

public record CursorSliceResponse<T>(List<T> content, boolean hasNext, Long nextCursor) {

    public static <T> CursorSliceResponse<T> from(CursorSliceResult<T> result) {
        return new CursorSliceResponse<>(
            result.content(),
            result.hasNext(),
            result.nextCursor()
        );
    }
}
