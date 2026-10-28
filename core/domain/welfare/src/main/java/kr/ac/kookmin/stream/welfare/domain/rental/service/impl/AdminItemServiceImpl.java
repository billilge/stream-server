package kr.ac.kookmin.stream.welfare.domain.rental.service.impl;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCreateCommand;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemType;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemUpdateCommand;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;
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
    // 물품 삭제 차단(진행 중인 대여 확인)에 쓴다. Item과 RentalHistory는 같은 도메인(rental) 안의 집합체라
    // UseCase를 거치지 않고 레포지토리를 바로 참조한다 — api:app-api의 RentalApplyUseCase/RentalReturnUseCase가
    // Service 레이어(ItemService/RentalHistoryService)를 조합하는 것과는 다른 층위다.
    private final RentalHistoryRepository rentalHistoryRepository;

    // 단일 조회라 트랜잭션이 필요 없다(coding-style.md 2-7절).
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
        Item item = Item.of(null, command.name(), command.category(), command.type(), command.count(), command.imageKey(), command.returnPolicy());
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item update(Long itemId, ItemUpdateCommand command) {
        Item existing = itemRepository.findById(itemId)
            .orElseThrow(() -> new BusinessException(RentalErrorCode.ITEM_NOT_FOUND));
        if (!existing.getName().equals(command.name()) && itemRepository.existsByNameAndIdNot(command.name(), itemId)) {
            throw new BusinessException(RentalErrorCode.ITEM_NAME_DUPLICATED);
        }

        String imageKey = command.imageKey() != null ? command.imageKey() : existing.getImageKey();
        Item updated = Item.of(itemId, command.name(), command.category(), command.type(), command.count(), imageKey, command.returnPolicy());
        return itemRepository.save(updated);
    }

    @Override
    @Transactional
    public void delete(Long itemId) {
        itemRepository.findById(itemId).orElseThrow(() -> new BusinessException(RentalErrorCode.ITEM_NOT_FOUND));
        if (rentalHistoryRepository.existsActiveRentalForItem(itemId)) {
            throw new BusinessException(RentalErrorCode.ITEM_HAS_ACTIVE_RENTAL);
        }
        itemRepository.delete(itemId);
    }
}
