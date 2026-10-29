package kr.ac.kookmin.stream.api.admin.welfare.rental.usecase;

import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.api.admin.welfare.rental.response.AdminRentalStatusWorkerLogResponse;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatusWorkerLog;
import kr.ac.kookmin.stream.welfare.domain.rental.service.AdminRentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 대여 이력 하나의 상태 변경 처리자 로그 조회. {@code rental}(로그)과 {@code member}(처리자 이름) 두 도메인을 조합한다. */
@Component
@RequiredArgsConstructor
public class AdminRentalStatusWorkerLogUseCase {

    private final AdminRentalHistoryService adminRentalHistoryService;
    private final MemberService memberService;

    public AdminRentalStatusWorkerLogResponse getWorkerLogs(Long historyId) {
        List<RentalStatusWorkerLog> logs = adminRentalHistoryService.getWorkerLogs(historyId);

        List<Long> workerIds = logs.stream().map(RentalStatusWorkerLog::getWorkerId).distinct().toList();
        Map<Long, Member> workerMap = memberService.getMapByIds(workerIds);

        return AdminRentalStatusWorkerLogResponse.from(logs, workerMap);
    }
}
