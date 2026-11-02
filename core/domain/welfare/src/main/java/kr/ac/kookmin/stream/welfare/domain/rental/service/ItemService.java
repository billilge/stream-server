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
     * @param keyword  이름 검색어. null이거나 공백이면 검색하지 않는다
     * @param cursor   이전 페이지의 마지막 항목. null이면 첫 페이지
     */
    CursorSliceResult<Item> getItems(ItemCategory category, String keyword, ItemCursor cursor, int size);

    /**
     * 재고를 차감한다.
     * <p>
     * 읽은 재고 값과 비교하는 낙관적 락으로 동시 차감을 감지한다. 그 사이 다른 트랜잭션이 재고를 바꿨으면
     * 커밋할 때 {@code OptimisticLockingFailureException}으로 실패한다. 호출부는 {@code LockExecutor}로 감싸
     * 시도마다 새 트랜잭션에서 실행해야 충돌한 요청이 다시 시도된다.
     */
    Item decreaseStock(Long itemId, int amount);

    /**
     * 재고를 복구한다(반납, 대여 취소/반려 시).
     * <p>
     * "상한이 없다"는 더 늘어나면 안 된다는 비즈니스 규칙이 없다는 뜻일 뿐, {@link #decreaseStock}과 똑같은 낙관적
     * 락 충돌 감지를 받는다 — 같은 물품에 다른 트랜잭션이 먼저 count를 바꾸면 똑같이 {@code OptimisticLockingFailureException}이
     * 난다. 호출부는 {@code decreaseStock}과 같은 이유로 {@code LockExecutor}로 감싸야 한다.
     */
    Item increaseStock(Long itemId, int amount);
}
