package kr.ac.kookmin.stream.api.admin.welfare.rental.usecase;

import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.service.AdminRentalHistoryService;
import kr.ac.kookmin.stream.welfare.domain.rental.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 관리자 대여 기록 삭제. {@code rental}(이력·처리자 로그 삭제)과 {@code item}(재고 복구) 두 도메인을 조합한다. */
@Component
@RequiredArgsConstructor
public class AdminRentalRecordDeleteUseCase {

    private final AdminRentalHistoryService adminRentalHistoryService;
    private final ItemService itemService;

    @Transactional
    public void delete(Long historyId) {
        RentalRecord record = adminRentalHistoryService.getById(historyId);

        // 진행 중인(아직 끝나지 않은) 기록이면 삭제 전에 재고를 복구한다
        if (!record.history().getRentalStatus().isFinished()) {
            itemService.increaseStock(record.history().getItemId(), record.history().getRentedCount());
        }

        adminRentalHistoryService.delete(historyId);
    }
}
