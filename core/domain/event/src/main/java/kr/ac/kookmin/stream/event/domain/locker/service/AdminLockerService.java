package kr.ac.kookmin.stream.event.domain.locker.service;

import java.util.List;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodCreateCommand;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodSummary;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodUpdateCommand;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerStatus;

/**
 * 운영진 사물함 API가 쓰는 서비스.
 */
public interface AdminLockerService {

    /**
     * 운영 회차 전체를 신청 시작 일시 최신순으로 조회한다. 아직 게시하지 않은 회차도 포함한다.
     */
    List<LockerPeriodSummary> getAllPeriods();

    /**
     * 운영 회차 한 건. 아직 게시하지 않은 회차도 포함한다.
     */
    LockerPeriodSummary getPeriodById(Long lockerPeriodId);

    /**
     * 운영 회차를 새로 만든다. 같은 학기가 이미 있거나 다른 회차와 기간이 겹치면 거부한다.
     * 만들어진 회차는 바로 게시 상태가 된다.
     */
    void createPeriod(LockerPeriodCreateCommand command);

    /**
     * 운영 회차의 신청·사용 기간을 바꾼다. 종료된 회차는 바꿀 수 없고, 진행 중인 회차는 신청 시작일을 바꿀 수 없다.
     * 다른 회차와 기간이 겹쳐도 거부한다.
     */
    void updatePeriod(Long lockerPeriodId, LockerPeriodUpdateCommand command);

    /**
     * 운영 회차를 소프트 삭제한다. 신청이 한 건이라도 있으면 거부한다.
     */
    void deletePeriod(Long lockerPeriodId);

    /**
     * 구역 안의 사물함 여러 개의 상태를 한 번에 바꾼다.
     * <p>
     * 하나라도 바꿀 수 없으면 아무것도 바꾸지 않는다. 사용 기간이 끝나지 않은 회차에 배정된 사물함은
     * 이용 중인 학생이 있으므로 {@code DISABLED}로 바꿀 수 없다.
     */
    void changeLockerStatus(Long sectionId, List<Long> lockerIds, LockerStatus status);
}
