package kr.ac.kookmin.stream.db.welfare;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.common.PreviewResult;
import kr.ac.kookmin.stream.db.common.ConstraintViolationUtil;
import kr.ac.kookmin.stream.db.common.LikePatterns;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCursor;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemType;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private static final String NAME_UNIQUE_CONSTRAINT = "uk_items_name";

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
    public List<Item> findAll() {
        return itemJpaRepository.findAllByOrderByIdAsc().stream()
            .map(ItemJpaEntity::toDomain)
            .toList();
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
    public Item getById(Long id) {
        return findById(id).orElseThrow(() -> new BusinessException(RentalErrorCode.ITEM_NOT_FOUND));
    }

    @Override
    public Item save(Item item) {
        try {
            // 동시 등록으로 이름 중복 검사를 함께 통과한 경우 유니크 인덱스 위반을 이 자리에서 잡으려면 바로 반영해야 한다
            return itemJpaRepository.saveAndFlush(ItemJpaEntity.from(item)).toDomain();
        } catch (DataIntegrityViolationException e) {
            if (ConstraintViolationUtil.isViolated(e, NAME_UNIQUE_CONSTRAINT)) {
                throw new BusinessException(RentalErrorCode.ITEM_NAME_DUPLICATED);
            }
            throw e;
        }
    }

    // 첫 페이지 내용과 전체 수가 곧 미리보기다
    @Override
    public PreviewResult<Item> findSearchPreview(String keyword, int limit) {
        Page<ItemJpaEntity> result = itemJpaRepository.findSearchPreview(
            LikePatterns.escape(keyword), Pageable.ofSize(limit)
        );

        return new PreviewResult<>(
            result.getContent().stream().map(ItemJpaEntity::toDomain).toList(),
            result.getTotalElements()
        );
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
