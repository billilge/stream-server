package kr.ac.kookmin.stream.event.domain.locker.service.impl;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplication;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationResult;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplyCommand;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import kr.ac.kookmin.stream.event.domain.locker.repository.LockerRepository;
import kr.ac.kookmin.stream.event.domain.locker.service.LockerApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class LockerApplicationServiceImpl implements LockerApplicationService {

    private final LockerRepository lockerRepository;

    @Override
    @Transactional
    public LockerApplicationResult apply(Long memberId, LockerApplyCommand command) {
        LockerPeriod period = getPublishedPeriod(command.lockerPeriodId());
        Locker locker = getSelectableLocker(period.getId(), command.lockerId());

        LockerApplication application = lockerRepository.saveApplication(
            LockerApplication.create(period.getId(), memberId, locker.getId(), LocalDateTime.now())
        );
        return new LockerApplicationResult(application, locker, period);
    }

    /** 게시된 운영 회차. 아직 공개하지 않은 회차는 학생에게 없는 것으로 보여야 하므로 구역 조회와 같은 기준으로 거른다. */
    private LockerPeriod getPublishedPeriod(Long lockerPeriodId) {
        return lockerRepository.findPublishedPeriodById(lockerPeriodId)
            .orElseThrow(() -> new BusinessException(LockerErrorCode.LOCKER_PERIOD_NOT_FOUND));
    }

    /**
     * 해당 운영 회차에 선택할 수 있는 사물함. 구역 조회와 같은 기준({@link Locker#isSelectable})으로 판정한다.
     * <p>
     * 흔한 경우를 먼저 거르는 검사일 뿐이다. 이 검사와 저장 사이에 끼어든 신청은 저장 시 유니크 제약이 막는다.
     */
    private Locker getSelectableLocker(Long lockerPeriodId, Long lockerId) {
        // 구역 상세에서 선택 가능한 사물함만 고를 수 있어 정상 흐름에서는 없는 사물함이 들어오지 않는다
        Locker locker = lockerRepository.findLockerById(lockerId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사물함입니다. lockerId=" + lockerId));

        if (!locker.isSelectable(lockerRepository.existsApplication(lockerPeriodId, lockerId))) {
            throw new BusinessException(LockerErrorCode.LOCKER_ALREADY_ASSIGNED);
        }
        return locker;
    }
}
