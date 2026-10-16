package kr.ac.kookmin.stream.event.domain.locker.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodCreateCommand;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodSummary;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodUpdateCommand;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSection;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionDetail;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionSummary;
import kr.ac.kookmin.stream.event.domain.locker.domain.SectionAvailabilityStatus;
import kr.ac.kookmin.stream.event.domain.locker.repository.LockerApplicationRepository;
import kr.ac.kookmin.stream.event.domain.locker.repository.LockerRepository;
import kr.ac.kookmin.stream.event.domain.locker.service.LockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class LockerServiceImpl implements LockerService {

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
    @Transactional(readOnly = true)
    public List<LockerSectionSummary> getSections(Long lockerPeriodId) {
        requirePublishedPeriod(lockerPeriodId);

        Set<Long> appliedLockerIds = lockerApplicationRepository.findAppliedLockerIds(lockerPeriodId);
        Map<Long, List<Locker>> lockersBySection = getLockerMapBySectionId();

        return lockerRepository.findAllSections().stream()
            .map(section -> summarize(
                section,
                // 사물함이 한 건도 없는 구역은 묶음에 키가 없다. 빈 목록으로 채워 목록에서 빠지지 않게 한다
                lockersBySection.getOrDefault(section.getId(), List.of()),
                appliedLockerIds
            ))
            .toList();
    }

    @Override
    public LockerSectionDetail getSectionDetail(Long lockerPeriodId, Long sectionId) {
        requirePublishedPeriod(lockerPeriodId);
        LockerSection section = lockerRepository.findSectionById(sectionId)
            .orElseThrow(() -> new BusinessException(LockerErrorCode.LOCKER_SECTION_NOT_FOUND));

        return new LockerSectionDetail(
            section,
            lockerRepository.findLayoutBySectionId(sectionId).orElse(null),
            lockerRepository.findLockersBySectionId(sectionId)
        );
    }

    @Override
    public Set<Long> getAppliedLockerIds(Long lockerPeriodId) {
        return lockerApplicationRepository.findAppliedLockerIds(lockerPeriodId);
    }

    @Override
    public Map<Long, List<Locker>> getLockerMapBySectionId() {
        return lockerRepository.findAllLockers().stream()
            .collect(Collectors.groupingBy(Locker::getSectionId));
    }

    @Override
    public Optional<Locker> getLockerByMemberId(Long lockerPeriodId, Long memberId) {
        return lockerApplicationRepository.findAppliedLockerId(lockerPeriodId, memberId)
            .flatMap(lockerRepository::findLockerById);
    }

    /**
     * 구역 하나의 전체·선택 가능 수를 센다.
     *
     * @param lockers          구역에 속한 사물함. 사물함이 없는 구역이면 빈 목록이다
     * @param appliedLockerIds 해당 운영 회차에 이미 신청된 사물함 식별자
     */
    private LockerSectionSummary summarize(
        LockerSection section,
        List<Locker> lockers,
        Set<Long> appliedLockerIds
    ) {
        int totalCount = lockers.size();
        int availableCount = (int) lockers.stream()
            .filter(locker -> locker.isSelectable(appliedLockerIds.contains(locker.getId())))
            .count();

        return new LockerSectionSummary(
            section.getId(),
            section.getLabel(),
            availableCount,
            totalCount,
            SectionAvailabilityStatus.from(availableCount, totalCount)
        );
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

    /** 아직 게시하지 않은 회차는 학생에게 없는 것으로 보여야 하므로 두 조회의 입구에서 같은 기준으로 거른다. */
    private void requirePublishedPeriod(Long lockerPeriodId) {
        if (!lockerRepository.existsPublishedPeriod(lockerPeriodId)) {
            throw new BusinessException(LockerErrorCode.LOCKER_PERIOD_NOT_FOUND);
        }
    }
}
