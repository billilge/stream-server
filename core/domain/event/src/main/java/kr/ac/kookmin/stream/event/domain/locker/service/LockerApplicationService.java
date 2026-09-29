package kr.ac.kookmin.stream.event.domain.locker.service;

import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationResult;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplyCommand;

/** 사물함 신청. 구역·배치 조회는 {@link LockerService}가 맡는다. */
public interface LockerApplicationService {

    /**
     * 고른 사물함을 신청하고 즉시 배정한다. 같은 사물함에 동시에 신청하면 먼저 저장된 신청만 성공한다.
     */
    LockerApplicationResult apply(Long memberId, LockerApplyCommand command);
}
