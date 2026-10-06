package kr.ac.kookmin.stream.event.domain.locker.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode;
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
    @Transactional(readOnly = true)
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

    /** 아직 게시하지 않은 회차는 학생에게 없는 것으로 보여야 하므로 두 조회의 입구에서 같은 기준으로 거른다. */
    private void requirePublishedPeriod(Long lockerPeriodId) {
        if (!lockerRepository.existsPublishedPeriod(lockerPeriodId)) {
            throw new BusinessException(LockerErrorCode.LOCKER_PERIOD_NOT_FOUND);
        }
    }
}
