package kr.ac.kookmin.stream;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import kr.ac.kookmin.stream.common.PageOffset;

public record PageQuery(

    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
    Integer page,

    @Min(value = 1, message = "조회 개수는 1 이상 100 이하여야 합니다.")
    @Max(value = 100, message = "조회 개수는 1 이상 100 이하여야 합니다.")
    Integer size
) {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public PageOffset toOffset() {
        return PageOffset.of(
            page == null ? DEFAULT_PAGE : page,
            size == null ? DEFAULT_SIZE : size
        );
    }
}
