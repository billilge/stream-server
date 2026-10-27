package kr.ac.kookmin.stream.event.domain.locker.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplication;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationStatus;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodCreateCommand;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodSummary;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodUpdateCommand;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerStatus;
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

    @Override
    @Transactional
    public void changeLockerStatus(Long sectionId, List<Long> lockerIds, LockerStatus status) {
        List<Locker> lockers = findLockersInSection(sectionId, lockerIds);
        if (!status.isUsable()) {
            requireNotInUse(lockerIds);
        }

        lockers.forEach(locker -> locker.changeStatus(status));
        lockerRepository.saveLockers(lockers);
    }

    /**
     * 요청한 사물함을 구역 소속으로 확인하고 돌려준다. 하나라도 그 구역에 없으면 아무것도 바꾸지 않도록 여기서 끊는다.
     */
    private List<Locker> findLockersInSection(Long sectionId, List<Long> lockerIds) {
        Map<Long, Locker> lockersInSection = lockerRepository.findLockersBySectionId(sectionId).stream()
            .collect(Collectors.toMap(Locker::getId, Function.identity()));

        return lockerIds.stream()
            .map(lockerId -> {
                Locker locker = lockersInSection.get(lockerId);
                if (locker == null) {
                    throw new BusinessException(LockerErrorCode.LOCKER_NOT_IN_SECTION);
                }
                return locker;
            })
            .toList();
    }

    /**
     * 아직 배정 상태인 신청이 걸린 사물함이 하나라도 있으면 끊는다.
     * <p>
     * 배정인지 만료인지는 내 신청 내역 화면과 같은 기준을 써야 하므로 {@link LockerApplicationStatus}에 맡긴다.
     * 저장소는 신청과 회차를 읽어 오기만 한다.
     */
    private void requireNotInUse(List<Long> lockerIds) {
        List<LockerApplication> applications = lockerApplicationRepository.findByLockerIds(lockerIds);
        if (applications.isEmpty()) {
            return;
        }

        LocalDate today = LocalDate.now();
        Set<Long> assignedPeriodIds = lockerRepository.findPublishedPeriodsByIds(applications.stream()
                .map(LockerApplication::getLockerPeriodId)
                .collect(Collectors.toSet())).stream()
            .filter(period -> LockerApplicationStatus.from(period, today) == LockerApplicationStatus.ASSIGNED)
            .map(LockerPeriod::getId)
            .collect(Collectors.toSet());

        boolean inUse = applications.stream()
            .anyMatch(application -> assignedPeriodIds.contains(application.getLockerPeriodId()));
        if (inUse) {
            throw new BusinessException(LockerErrorCode.LOCKER_IN_USE);
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
