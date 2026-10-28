package kr.ac.kookmin.stream.db.welfare;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.db.common.LikePatterns;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCursor;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemType;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
            category, LikePatterns.escape(keyword),
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

    @Override
    public Optional<Item> findById(Long id) {
        return itemJpaRepository.findById(id).map(ItemJpaEntity::toDomain);
    }

    @Override
    public Item save(Item item) {
        return itemJpaRepository.save(ItemJpaEntity.from(item)).toDomain();
    }

    @Override
    public PageResult<Item> search(ItemCategory category, ItemType type, String keyword, PageOffset pageOffset) {
        Page<ItemJpaEntity> result = itemJpaRepository.search(
            category, type, LikePatterns.escape(keyword), PageRequest.of(pageOffset.page(), pageOffset.size())
        );

        return PageResult.of(
            result.getContent().stream().map(ItemJpaEntity::toDomain).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements()
        );
    }

    @Override
    public boolean existsByName(String name) {
        return itemJpaRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, Long id) {
        return itemJpaRepository.existsByNameAndIdNot(name, id);
    }

    @Override
    public void delete(Long id) {
        itemJpaRepository.deleteById(id);
    }
}
