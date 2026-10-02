package kr.ac.kookmin.stream.db.welfare;

import java.util.Collection;
import java.util.List;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCursor;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemJpaRepository itemJpaRepository;

    @Override
    public CursorSliceResult<Item> findSlice(ItemCategory category, String keyword, ItemCursor cursor, int size) {
        Pageable pageable = Pageable.ofSize(size + 1);
        List<ItemJpaEntity> entities = itemJpaRepository.findSlice(
            category, keyword,
            cursor == null ? null : cursor.name(),
            cursor == null ? null : cursor.id(),
            pageable
        );

        return CursorSliceResult.ofSlice(
            entities, size, ItemJpaEntity::toDomain, item -> ItemCursor.of(item).format()
        );
    }

    @Override
    public List<Item> findAllByIds(Collection<Long> ids) {
        return itemJpaRepository.findAllById(ids).stream()
            .map(ItemJpaEntity::toDomain)
            .toList();
    }
}
