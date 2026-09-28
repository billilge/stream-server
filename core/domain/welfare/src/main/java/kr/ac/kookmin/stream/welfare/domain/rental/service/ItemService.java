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

    /**
     * 재고를 비관적 락으로 안전하게 차감한다. 이 메서드는 항상 비관적 락 조회로 시작해야 한다 —
     * 호출부(대여 신청)의 트랜잭션에서 이 호출이 첫 조회가 되어야, 뒤이은 다른 평범한 조회들이
     * 락 획득 이후 시점의 데이터를 보게 된다.
     */
    Item decreaseStock(Long itemId, int amount);
}
