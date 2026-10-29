package kr.ac.kookmin.stream.welfare.domain.rental.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCursor;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemType;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;

public interface ItemRepository {

    /**
     * 물품을 이름 오름차순(같으면 식별자 오름차순)으로 커서 페이지 단위로 조회한다. 학생 앱용.
     *
     * @param category 카테고리. null이면 전체
     * @param keyword  이름에 포함된 검색어. null이거나 공백이면 검색하지 않는다. 앞뒤 공백은 지우고 {@code %}·{@code _}는 글자 그대로 검색한다
     * @param cursor   이전 페이지의 마지막 항목. null이면 첫 페이지
     */
    CursorSliceResult<Item> findSlice(ItemCategory category, String keyword, ItemCursor cursor, int size);

    /**
     * 물품을 이름 오름차순(같으면 식별자 오름차순)으로 페이지 번호 단위로 조회한다. 관리자 콘솔용.
     *
     * @param category 카테고리. null이면 전체
     * @param type     물품 구분. null이면 전체
     * @param keyword  이름에 포함된 검색어. null이거나 공백이면 검색하지 않는다
     */
    PageResult<Item> search(ItemCategory category, ItemType type, String keyword, PageOffset pageOffset);

    List<Item> findAllByIds(Collection<Long> ids);

    Optional<Item> findById(Long id);

    /** id로 조회한다. 없으면 ITEM_NOT_FOUND — 서비스마다 반복되던 findById().orElseThrow()를 여기로 모았다. */
    default Item getById(Long id) {
        return findById(id).orElseThrow(() -> new BusinessException(RentalErrorCode.ITEM_NOT_FOUND));
    }

    boolean existsByName(String name);

    /** 자기 자신(id)은 제외하고 이름 중복을 확인한다. 물품 수정에서 쓴다. */
    boolean existsByNameAndIdNot(String name, Long id);

    Item save(Item item);

    /** 하드 삭제. 존재하지 않으면 아무 일도 하지 않는다(존재 확인은 호출부가 먼저 한다). */
    void delete(Long id);
}
