package kr.ac.kookmin.stream.event.domain.locker.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodCreateCommand;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodSummary;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodUpdateCommand;
import kr.ac.kookmin.stream.event.domain.locker.repository.LockerApplicationRepository;
import kr.ac.kookmin.stream.event.domain.locker.repository.LockerRepository;
import kr.ac.kookmin.stream.event.domain.locker.service.AdminLockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class AdminLockerServiceImpl implements AdminLockerService {

    private final LockerRepository lockerRepository;
    private final LockerApplicationRepository lockerApplicationRepository;

    @Override
    public List<LockerPeriodSummary> getAllPeriods() {
        LocalDateTime now = LocalDateTime.now();
        return lockerRepository.findAllPeriods().stream()
            .map(period -> LockerPeriodSummary.of(period, now))
            .toList();
    }

    @Override
    public LockerPeriodSummary getPeriodById(Long lockerPeriodId) {
        return LockerPeriodSummary.of(getPeriod(lockerPeriodId), LocalDateTime.now());
    }

    @Override
    @Transactional
    public void createPeriod(LockerPeriodCreateCommand command) {
        LockerPeriod period = LockerPeriod.create(command);
        validateNotOverlapped(period);

        lockerRepository.savePeriod(period);
    }

    @Override
    @Transactional
    public void updatePeriod(Long lockerPeriodId, LockerPeriodUpdateCommand command) {
        LockerPeriod period = getPeriod(lockerPeriodId);
        period.update(command, LocalDateTime.now());
        validateNotOverlapped(period);

        lockerRepository.savePeriod(period);
    }

    @Override
    @Transactional
    public void deletePeriod(Long lockerPeriodId) {
        // 신청이 딸린 회차를 지우면 신청이 가리킬 회차가 없어진다. FK가 없어 DB가 막아주지 않는다
        if (lockerApplicationRepository.existsByPeriod(lockerPeriodId)) {
            throw new BusinessException(LockerErrorCode.LOCKER_PERIOD_HAS_APPLICATIONS);
        }
        if (!lockerRepository.deletePeriod(lockerPeriodId)) {
            throw new BusinessException(LockerErrorCode.LOCKER_PERIOD_NOT_FOUND);
        }
    }

    private LockerPeriod getPeriod(Long lockerPeriodId) {
        return lockerRepository.findPeriodById(lockerPeriodId)
            .orElseThrow(() -> new BusinessException(LockerErrorCode.LOCKER_PERIOD_NOT_FOUND));
    }

    /**
     * 회차는 같은 시기에 둘이 돌 수 없다. 회차 수가 한 학기에 한두 건이라 전부 읽어 메모리에서 견준다.
     * 자기 자신은 식별자로 걸러낸다(등록이면 식별자가 없어 전부 비교 대상이다).
     */
    private void validateNotOverlapped(LockerPeriod period) {
        boolean overlapped = lockerRepository.findAllPeriods().stream()
            .filter(other -> !other.getId().equals(period.getId()))
            .anyMatch(period::overlaps);
        if (overlapped) {
            throw new BusinessException(LockerErrorCode.LOCKER_PERIOD_OVERLAPPED);
        }
    }
}
