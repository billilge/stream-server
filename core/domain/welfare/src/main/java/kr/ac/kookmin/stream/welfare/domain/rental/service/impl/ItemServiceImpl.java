package kr.ac.kookmin.stream.welfare.domain.rental.service.impl;

import java.util.List;
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

    @Override
    public PreviewResult<Item> searchPreview(String keyword, int limit) {
        return itemRepository.findSearchPreview(keyword, limit);
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
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
