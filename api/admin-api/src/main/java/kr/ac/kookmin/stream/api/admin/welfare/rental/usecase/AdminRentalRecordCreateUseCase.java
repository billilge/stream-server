package kr.ac.kookmin.stream.api.admin.welfare.rental.usecase;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.api.admin.welfare.rental.request.AdminRentalRecordCreateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.rental.response.AdminRentalHistoryResponse;
import kr.ac.kookmin.stream.common.LockExecutor;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.service.ItemService;
import kr.ac.kookmin.stream.welfare.domain.rental.service.RentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 관리자 임의 대여 기록 추가. {@code member}(대여자·근무자 확인)와 {@code rental}(재고 차감, 이력 생성) 도메인을 조합한다. */
@Component
@RequiredArgsConstructor
public class AdminRentalRecordCreateUseCase {

    // 등록 폼에 수량 입력란이 없어 항상 1건으로 등록한다.
    private static final int FIXED_COUNT = 1;

    private final LockExecutor lockExecutor;
    private final MemberService memberService;
    private final ItemService itemService;
    private final RentalHistoryService rentalHistoryService;

    public AdminRentalHistoryResponse create(AdminRentalRecordCreateRequest request) {
        return lockExecutor.executeOptimistic(() -> {
            Member member = memberService.getById(request.memberId());
            memberService.getById(request.workerId());

            Item item = itemService.decreaseStock(request.itemId(), FIXED_COUNT);
            RentalHistory history = rentalHistoryService.create(
                item, request.memberId(), request.workerId(), FIXED_COUNT,
                LocalDateTime.now(), request.rentAt()
            );
            return AdminRentalHistoryResponse.from(new RentalRecord(history, item), member);
        });
    }
}
