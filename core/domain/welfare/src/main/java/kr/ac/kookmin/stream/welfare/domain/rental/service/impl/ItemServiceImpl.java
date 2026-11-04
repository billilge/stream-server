package kr.ac.kookmin.stream.welfare.domain.rental.service.impl;

import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.common.PreviewResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCursor;
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

    // 미리보기와 전체 수를 두 문장으로 조회한다. 저장소의 @Query 메서드에는 트랜잭션이 붙지 않으므로, 두 조회가 같은
    // 스냅샷을 보도록 읽기 트랜잭션으로 묶는다
    @Override
    @Transactional(readOnly = true)
    public PreviewResult<Item> searchPreview(String keyword, int limit) {
        return itemRepository.findSearchPreview(keyword, limit);
    }

    @Override
    @Transactional
    public Item decreaseStock(Long itemId, int amount) {
        Item item = itemRepository.getById(itemId);
        item.decreaseStock(amount);
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item increaseStock(Long itemId, int amount) {
        Item item = itemRepository.getById(itemId);
        item.increaseStock(amount);
        return itemRepository.save(item);
    }
}
