package kr.ac.kookmin.stream.welfare.domain.rental.service;

import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCursor;

public interface ItemService {

    /**
     * 물품 목록을 이름순 커서 페이지로 조회한다.
     *
     * @param category 카테고리. null이면 전체
     * @param keyword  이름 검색어. null이면 검색하지 않는다
     * @param cursor   이전 페이지의 마지막 항목. null이면 첫 페이지
     */
    CursorSliceResult<Item> getItems(ItemCategory category, String keyword, ItemCursor cursor, int size);
}
