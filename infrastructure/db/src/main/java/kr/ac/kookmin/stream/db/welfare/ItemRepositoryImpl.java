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

    private static final char LIKE_ESCAPE = '!';

    private final ItemJpaRepository itemJpaRepository;

    @Override
    public CursorSliceResult<Item> findSlice(ItemCategory category, String keyword, ItemCursor cursor, int size) {
        Pageable pageable = Pageable.ofSize(size + 1);
        String keywordPattern = keyword == null ? null : toContainsPattern(keyword);
        List<ItemJpaEntity> entities = cursor == null
            ? itemJpaRepository.findFirstSlice(category, keywordPattern, pageable)
            : itemJpaRepository.findNextSlice(category, keywordPattern, cursor.name(), cursor.id(), pageable);

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

    // 검색어를 "포함" 조건의 LIKE 패턴으로 바꾼다. 검색어 안의 와일드카드가 패턴으로 해석되지 않게 이스케이프한다
    private String toContainsPattern(String keyword) {
        String escaped = keyword
            .replace(String.valueOf(LIKE_ESCAPE), "" + LIKE_ESCAPE + LIKE_ESCAPE)
            .replace("%", LIKE_ESCAPE + "%")
            .replace("_", LIKE_ESCAPE + "_");
        return "%" + escaped + "%";
    }
}
