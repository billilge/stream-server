package kr.ac.kookmin.stream.welfare.domain.rental.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCursor;

public interface ItemRepository {

    /**
     * 물품을 이름 오름차순(같으면 식별자 오름차순)으로 커서 페이지 단위로 조회한다.
     *
     * @param category 카테고리. null이면 전체
     * @param keyword  이름에 포함된 검색어. null이면 검색하지 않는다
     * @param cursor   이전 페이지의 마지막 항목. null이면 첫 페이지
     */
    CursorSliceResult<Item> findSlice(ItemCategory category, String keyword, ItemCursor cursor, int size);

    List<Item> findAllByIds(Collection<Long> ids);

    /** 재고 차감용 비관적 락 조회. 동시에 들어온 대여 신청이 같은 물품의 재고를 동시에 읽지 못하게 한다. */
    Optional<Item> findByIdForUpdate(Long id);

    Item save(Item item);
}
