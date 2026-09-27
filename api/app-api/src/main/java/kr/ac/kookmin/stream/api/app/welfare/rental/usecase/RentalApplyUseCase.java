package kr.ac.kookmin.stream.api.app.welfare.rental.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.welfare.domain.fee.service.PayerService;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.ItemRepository;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.RentalHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 대여 신청. {@code fee}(회비 납부 확인)와 {@code rental}(재고 차감, 이력 생성) 두 도메인을 조합한다.
 * 대여 시간(영업시간·점심시간) 검증은 DB에 닿지 않으므로 이 트랜잭션에 들어오기 전, 컨트롤러에서 끝낸다.
 */
@Component
@RequiredArgsConstructor
public class RentalApplyUseCase {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final PayerService payerService;
    private final ItemRepository itemRepository;
    private final RentalHistoryRepository rentalHistoryRepository;

    @Transactional
    public void apply(Long memberId, Long itemId, int count, int rentAtHour, int rentAtMinute, boolean ignoreDuplicate) {
        if (!payerService.isPayer(memberId)) {
            throw new BusinessException(RentalErrorCode.MEMBER_IS_NOT_PAYER);
        }

        if (!ignoreDuplicate && rentalHistoryRepository.existsActiveRental(itemId, memberId)) {
            throw new BusinessException(RentalErrorCode.RENTAL_ITEM_DUPLICATED);
        }

        // 비관적 락으로 읽어서, 동시에 들어온 다른 신청이 이 물품의 재고를 같이 통과하지 못하게 한다
        Item item = itemRepository.findByIdForUpdate(itemId)
            .orElseThrow(() -> new BusinessException(RentalErrorCode.ITEM_NOT_FOUND));
        item.decreaseStock(count);
        itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now(KST);
        LocalDateTime rentAt = LocalDate.now(KST).atTime(rentAtHour, rentAtMinute);
        rentalHistoryRepository.save(RentalHistory.create(item, memberId, count, now, rentAt));
    }
}
