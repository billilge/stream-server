package kr.ac.kookmin.stream.event.domain.locker.service;

import java.util.List;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationResult;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplyCommand;

/** 사물함 신청과 그 이력. 구역·배치 조회는 {@link LockerService}가 맡는다. */
public interface LockerApplicationService {

    /**
     * 고른 사물함을 신청하고 즉시 배정한다. 같은 사물함에 동시에 신청하면 먼저 저장된 신청만 성공한다.
     */
    LockerApplicationResult apply(Long memberId, LockerApplyCommand command);

    /**
     * 게시된 운영 회차에서 회원이 신청한 사물함 내역을 신청 일시 최신순으로 조회한다. 없으면 빈 목록이다.
     */
    List<LockerApplicationResult> getApplicationsByMemberId(Long memberId);
}
