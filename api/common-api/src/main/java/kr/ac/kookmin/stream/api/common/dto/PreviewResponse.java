package kr.ac.kookmin.stream.api.common.dto;

import java.util.List;
import java.util.function.Function;
import kr.ac.kookmin.stream.common.PreviewResult;

/**
 * 앞의 몇 건과 전체 수. 홈처럼 "미리보기 N건 + 더보기"를 그리는 화면이 쓴다.
 * 이어서 더 읽지 않으므로 커서나 페이지 정보를 담지 않는다.
 */
public record PreviewResponse<T>(List<T> content, long totalCount) {

    public static <E, T> PreviewResponse<T> from(PreviewResult<E> result, Function<E, T> mapper) {
        return new PreviewResponse<>(
                result.content().stream().map(mapper).toList(),
                result.totalCount()
        );
    }
}
