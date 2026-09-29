package kr.ac.kookmin.stream.api.app.welfare.rental.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import kr.ac.kookmin.stream.api.common.CursorCodec;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCursor;

public record ItemListParams(
    String category,

    String keyword,

    String cursor,

    @Min(value = 1, message = "조회 개수는 1 이상 100 이하여야 합니다.")
    @Max(value = 100, message = "조회 개수는 1 이상 100 이하여야 합니다.")
    Integer size
) {

    private static final int DEFAULT_SIZE = 20;

    public ItemCategory toCategory() {
        return ItemCategory.from(category);
    }

    // 공백뿐인 검색어는 검색하지 않는 것과 같다
    public String toKeyword() {
        return keyword == null || keyword.isBlank() ? null : keyword.strip();
    }

    public ItemCursor toCursor() {
        return cursor == null ? null : ItemCursor.from(CursorCodec.decode(cursor));
    }

    public int sizeOrDefault() {
        return size == null ? DEFAULT_SIZE : size;
    }
}
