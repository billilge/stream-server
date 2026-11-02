package kr.ac.kookmin.stream.welfare.domain.rental.service.impl;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.LockExecutor;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCreateCommand;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemType;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemUpdateCommand;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ReturnPolicy;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.ItemRepository;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.RentalHistoryRepository;
import kr.ac.kookmin.stream.welfare.domain.rental.service.AdminItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class AdminItemServiceImpl implements AdminItemService {

    private final ItemRepository itemRepository;
    // 물품 삭제 차단(진행 중인 대여 확인)에 쓴다.
    private final RentalHistoryRepository rentalHistoryRepository;
    private final LockExecutor lockExecutor;

    @Override
    public PageResult<Item> search(ItemCategory category, ItemType type, String keyword, PageOffset pageOffset) {
        return itemRepository.search(category, type, keyword, pageOffset);
    }

    @Override
    @Transactional
    public Item create(ItemCreateCommand command) {
        if (itemRepository.existsByName(command.name())) {
            throw new BusinessException(RentalErrorCode.ITEM_NAME_DUPLICATED);
        }
        validateReturnPolicyRequired(command.type(), command.returnPolicy());
        Item item = Item.of(null, command.name(), command.category(), command.type(), command.count(), command.imageKey(), command.returnPolicy());
        return itemRepository.save(item);
    }

    // 재고 변경은 increaseStock/decreaseStock의 검증을 타도록 목표 수량과 현재 수량의 차이만큼 증감시킨다.
    @Override
    public Item update(Long itemId, ItemUpdateCommand command) {
        return lockExecutor.executeOptimistic(() -> {
            Item existing = itemRepository.getById(itemId);
            if (!existing.getName().equals(command.name()) && itemRepository.existsByNameAndIdNot(command.name(), itemId)) {
                throw new BusinessException(RentalErrorCode.ITEM_NAME_DUPLICATED);
            }
            validateReturnPolicyRequired(command.type(), command.returnPolicy());

            int countDiff = command.count() - existing.getCount();
            if (countDiff > 0) {
                existing.increaseStock(countDiff);
            } else if (countDiff < 0) {
                existing.decreaseStock(-countDiff);
            }

            String imageKey = command.imageKey() != null ? command.imageKey() : existing.getImageKey();
            Item updated = Item.of(itemId, command.name(), command.category(), command.type(), existing.getCount(), imageKey, command.returnPolicy());
            return itemRepository.save(updated);
        });
    }

    // 대여품(RENTAL)은 반납 정책이 필수다.
    private void validateReturnPolicyRequired(ItemType type, ReturnPolicy returnPolicy) {
        if (type == ItemType.RENTAL && returnPolicy == null) {
            throw new BusinessException(RentalErrorCode.RETURN_POLICY_REQUIRED);
        }
    }

    @Override
    @Transactional
    public void delete(Long itemId) {
        itemRepository.getById(itemId);
        if (rentalHistoryRepository.existsActiveRentalForItem(itemId)) {
            throw new BusinessException(RentalErrorCode.ITEM_HAS_ACTIVE_RENTAL);
        }
        itemRepository.delete(itemId);
    }
}
