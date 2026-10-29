package kr.ac.kookmin.stream.api.admin.welfare.rental.usecase;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.api.admin.welfare.rental.request.AdminRentalRecordCreateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.rental.response.AdminRentalHistoryResponse;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.DateUtil;
import kr.ac.kookmin.stream.common.LockExecutor;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.service.ItemService;
import kr.ac.kookmin.stream.welfare.domain.rental.service.RentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 관리자 임의 대여 기록 추가. {@code member}(대여자·근무자 확인)와 {@code rental}(재고 차감, 이력 생성) 도메인을
 * 조합한다. 재고 차감이 {@code decreaseStock}을 거치므로 {@code RentalApplyUseCase}와 같은 이유로
 * {@code LockExecutor}로 감싼다 — 같은 물품에 신청·등록이 몰려도 재고가 음수가 되지 않게 재시도한다.
 */
@Component
@RequiredArgsConstructor
public class AdminRentalRecordCreateUseCase {

    // 등록 폼(RentalRecordFormPage)에 수량 입력란이 없어 항상 1건으로 등록한다.
    private static final int FIXED_COUNT = 1;

    private final LockExecutor lockExecutor;
    private final MemberService memberService;
    private final ItemService itemService;
    private final RentalHistoryService rentalHistoryService;

    public AdminRentalHistoryResponse create(AdminRentalRecordCreateRequest request) {
        CreatedRental createdRental = lockExecutor.executeOptimistic(() -> {
            Member member = findMemberOrThrow(request.memberId(), RentalErrorCode.MEMBER_NOT_FOUND);
            findMemberOrThrow(request.workerId(), RentalErrorCode.WORKER_NOT_FOUND);

            Item item = itemService.decreaseStock(request.itemId(), FIXED_COUNT);
            RentalHistory history = rentalHistoryService.create(
                item, request.memberId(), request.workerId(), FIXED_COUNT,
                LocalDateTime.now(DateUtil.KST), request.rentAt()
            );
            return new CreatedRental(history, item, member);
        });

        return AdminRentalHistoryResponse.from(
            new RentalRecord(createdRental.history(), createdRental.item()), createdRental.member()
        );
    }

    private Member findMemberOrThrow(Long memberId, RentalErrorCode errorCode) {
        return memberService.findAllByIds(List.of(memberId)).stream()
            .findFirst()
            .orElseThrow(() -> new BusinessException(errorCode));
    }

    private record CreatedRental(RentalHistory history, Item item, Member member) {
    }
}
