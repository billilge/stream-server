package kr.ac.kookmin.stream.welfare.domain.rental.service.impl;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCursor;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.ItemRepository;
import kr.ac.kookmin.stream.welfare.domain.rental.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    // 조회 쿼리가 1개라 트랜잭션을 걸지 않는다. 쿼리가 늘어 한 스냅샷이 필요해지면 @Transactional(readOnly = true)를 붙인다.
    @Override
    public CursorSliceResult<Item> getItems(ItemCategory category, String keyword, ItemCursor cursor, int size) {
        return itemRepository.findSlice(category, keyword, cursor, size);
    }

    @Override
    @Transactional
    public Item decreaseStock(Long itemId, int amount) {
        Item item = itemRepository.findByIdForUpdate(itemId)
            .orElseThrow(() -> new BusinessException(RentalErrorCode.ITEM_NOT_FOUND));
        item.decreaseStock(amount);
        return itemRepository.save(item);
    }
}
