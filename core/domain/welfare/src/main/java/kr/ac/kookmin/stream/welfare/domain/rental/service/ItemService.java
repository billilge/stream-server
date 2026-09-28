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
     * 재고를 차감한다.
     * <p>
     * 동시성 보호(락)는 아직 없다 — 동시에 들어온 두 요청이 같은 물품의 재고를 동시에 통과해
     * 재고가 음수로 내려갈 수 있는 경합이 이론적으로 남아있다. 이 프로젝트 규모에서 실제로
     * 문제된 적은 없어 보이는 레거시(billilge/backend)와 같은 수준으로, 일단 보호 없이 간다
     * (`billilge-rental-apply-review-fixes.md` 참고 — 필요해지면 별도로 다시 도입한다).
     */
    Item decreaseStock(Long itemId, int amount);
}
