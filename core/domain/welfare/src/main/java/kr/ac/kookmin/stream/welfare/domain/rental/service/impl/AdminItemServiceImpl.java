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
    // UseCase를 거치지 않고 레포지토리를 바로 참조한다 — api:app-api의 RentalApplyUseCase/RentalCancelUseCase가
    // Service 레이어(ItemService/RentalHistoryService)를 조합하는 것과는 다른 층위다.
    private final RentalHistoryRepository rentalHistoryRepository;
    private final LockExecutor lockExecutor;

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

    // command.count()가 재고를 직접 덮어쓰면 낙관적 락(DIRTY) 대상인 Item.count를 건드리면서도 decreaseStock/
    // increaseStock의 검증(재고 부족 등)을 거치지 않는다 — 그래서 목표 수량과 현재 수량의 차이만큼 기존
    // 증감 메서드를 재사용한다. LockExecutor로 감싸는 것도, @Transactional을 안 거는 것도 decreaseStock/
    // increaseStock과 같은 이유다(시도마다 LockExecutor가 새 트랜잭션을 연다). 이름 중복 검증도 재시도마다
    // 다시 하는데, 가벼운 조회라 RentalApplyUseCase의 결제자 검증과 같은 트레이드오프다.
    @Override
    public Item update(Long itemId, ItemUpdateCommand command) {
        return lockExecutor.executeOptimistic(() -> {
            Item existing = itemRepository.getById(itemId);
            if (!existing.getName().equals(command.name()) && itemRepository.existsByNameAndIdNot(command.name(), itemId)) {
                throw new BusinessException(RentalErrorCode.ITEM_NAME_DUPLICATED);
            }

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
