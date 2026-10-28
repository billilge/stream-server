package kr.ac.kookmin.stream.event.domain.locker.service;

import java.util.List;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationResult;
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

    /**
     * 운영 회차의 신청 현황을 신청 일시 최신순으로 조회한다. 취소된 신청도 담는다.
     *
     * @param memberIds 회원으로 좁힐 때만 넘긴다. {@code null}이면 회차 전체를 본다
     */
    PageResult<LockerApplicationResult> getApplications(Long lockerPeriodId, List<Long> memberIds, PageOffset pageOffset);

    /**
     * 신청을 취소한다. 행은 남기고 표시만 바꿔 같은 학생·같은 칸에 다시 배정할 수 있게 한다.
     * <p>
     * 사용 기간이 끝난 회차의 신청은 되돌릴 자리가 없어 거부한다.
     */
    void cancelApplication(Long lockerApplicationId);

    /**
     * 운영진이 학생에게 사물함을 직접 배정한다. 신청 기간은 보지 않는다 — 신청을 놓친 학생이나 자리를 옮겨야 하는
     * 학생에게 쓰는 기능이라 기간이 끝난 뒤가 오히려 주 사용 시점이다. 사용 기간이 끝난 회차만 막는다.
     * <p>
     * 학생회비 납부자인지는 이 서비스가 보지 않는다. 교차 도메인이라 {@code api:admin-api}의 UseCase가 확인한다.
     */
    void assignLocker(Long lockerPeriodId, Long memberId, Long lockerId);
}
