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
        Long lockerPeriodId = command.lockerPeriodId();
        LockerPeriod period = lockerRepository.findPublishedPeriodById(lockerPeriodId)
            .orElseThrow(() -> new BusinessException(LockerErrorCode.LOCKER_PERIOD_NOT_FOUND));

        // 구역 상세에서 선택 가능한 사물함만 고를 수 있어 정상 흐름에서는 없는 사물함이 들어오지 않는다
        Locker locker = lockerRepository.findLockerById(command.lockerId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사물함입니다. lockerId=" + command.lockerId()));

        // 흔한 경우를 먼저 거르는 검사일 뿐이다. 이 검사와 저장 사이에 끼어든 신청은 저장 시 유니크 제약이 막는다
        if (!locker.isSelectable(lockerRepository.existsApplication(lockerPeriodId, locker.getId()))) {
            throw new BusinessException(LockerErrorCode.LOCKER_ALREADY_ASSIGNED);
        }

        LockerApplication application = lockerRepository.saveApplication(
            LockerApplication.create(lockerPeriodId, memberId, locker.getId(), LocalDateTime.now())
        );
        return new LockerApplicationResult(application, locker, period);
    }
}
