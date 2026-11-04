package kr.ac.kookmin.stream.api.admin.welfare.rental.usecase;

import java.util.List;
import kr.ac.kookmin.stream.api.admin.welfare.rental.response.AdminRentalHistoryResponse;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.service.AdminRentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 대여 기록 물품 코드 수정. {@code rental}(코드 변경)과 {@code member}(대여자 이름) 두 도메인을 조합한다. */
@Component
@RequiredArgsConstructor
public class AdminRentalItemCodeUpdateUseCase {

    private final AdminRentalHistoryService adminRentalHistoryService;
    private final MemberService memberService;

    public AdminRentalHistoryResponse updateItemCode(Long historyId, String itemCode) {
        RentalRecord record = adminRentalHistoryService.updateItemCode(historyId, itemCode);
        Member member = memberService.getMapByIds(List.of(record.history().getMemberId()))
            .get(record.history().getMemberId());
        return AdminRentalHistoryResponse.from(record, member);
    }
}
