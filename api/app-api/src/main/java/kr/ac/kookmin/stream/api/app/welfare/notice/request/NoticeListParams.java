package kr.ac.kookmin.stream.api.app.welfare.notice.request;

import jakarta.validation.constraints.Size;
import kr.ac.kookmin.stream.api.common.CursorCodec;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCursor;

/**
 * @param keyword 제목 검색어. 없거나 공백뿐이면 검색하지 않는다
 */
public record NoticeListParams(
    String category,

    @Size(max = 50, message = "검색어는 50자 이하로 입력해 주세요.")
    String keyword,

    String cursor,

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
