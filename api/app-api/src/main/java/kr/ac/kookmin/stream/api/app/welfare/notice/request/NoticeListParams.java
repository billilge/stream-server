package kr.ac.kookmin.stream.api.app.welfare.notice.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import kr.ac.kookmin.stream.api.common.CursorCodec;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCursor;

public record NoticeListParams(
    String category,

    String cursor,

    @Min(value = 1, message = "조회 개수는 1 이상 100 이하여야 합니다.")
    @Max(value = 100, message = "조회 개수는 1 이상 100 이하여야 합니다.")
    Integer size
) {

    private static final int DEFAULT_SIZE = 20;

    public NoticeCategory toCategory() {
        return NoticeCategory.from(category);
    }

    public NoticeCursor toCursor() {
        return cursor == null ? null : NoticeCursor.from(CursorCodec.decode(cursor));
    }

    public int sizeOrDefault() {
        return size == null ? DEFAULT_SIZE : size;
    }
}
