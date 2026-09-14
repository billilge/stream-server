package kr.ac.kookmin.stream;

import java.util.List;
import java.util.function.Function;
import kr.ac.kookmin.stream.common.CursorSliceResult;

public record CursorSliceResponse<T>(List<T> content, boolean hasNext, String nextCursor) {

    public static <T> CursorSliceResponse<T> from(CursorSliceResult<T> result) {
        return new CursorSliceResponse<>(
            result.content(),
            result.hasNext(),
            result.nextCursor()
        );
    }

    /**
     * 도메인 슬라이스를 응답으로 옮긴다. 커서는 클라이언트에게 불투명한 토큰이어야 하므로 여기서 인코딩한다.
     * <p>
     * 컨트롤러마다 같은 변환·인코딩을 되풀이하지 않도록 웹 계층 공통으로 둔다.
     *
     * @param mapper 도메인 객체를 응답 DTO로 옮기는 변환
     */
    public static <T, R> CursorSliceResponse<R> of(CursorSliceResult<T> result, Function<T, R> mapper) {
        return new CursorSliceResponse<>(
            result.content().stream().map(mapper).toList(),
            result.hasNext(),
            result.nextCursor() == null ? null : CursorCodec.encode(result.nextCursor())
        );
    }
}
