package kr.ac.kookmin.stream.event.domain.locker.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerAvailability;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionSummary;
import kr.ac.kookmin.stream.event.domain.locker.repository.LockerRepository;
import kr.ac.kookmin.stream.event.domain.locker.service.LockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class LockerServiceImpl implements LockerService {

    private final LockerRepository lockerRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LockerSectionSummary> getSections(Long lockerPeriodId, Long memberId) {
        requirePublishedPeriod(lockerPeriodId);

        Set<Long> appliedLockerIds = lockerRepository.findAppliedLockerIds(lockerPeriodId);
        Map<Long, List<Locker>> lockersBySection = lockerRepository.findAllLockers().stream()
            .collect(Collectors.groupingBy(Locker::getSectionId));

        Long myLockerId = lockerRepository.findAppliedLockerId(lockerPeriodId, memberId).orElse(null);

        return lockerRepository.findAllSections().stream()
            .map(section -> {
                // 사물함이 한 건도 없는 구역은 묶음에 키가 없다. 빈 목록으로 채워 목록에서 빠지지 않게 한다
                List<Locker> sectionLockers = lockersBySection.getOrDefault(section.getId(), List.of());
                return LockerSectionSummary.of(
                    section, sectionLockers, appliedLockerIds, containsMine(sectionLockers, myLockerId));
            })
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LockerAvailability> getSectionLockers(Long lockerPeriodId, Long sectionId, Long memberId) {
        requirePublishedPeriod(lockerPeriodId);
        requireSection(sectionId);

        Set<Long> appliedLockerIds = lockerRepository.findAppliedLockerIds(lockerPeriodId);
        Long myLockerId = lockerRepository.findAppliedLockerId(lockerPeriodId, memberId).orElse(null);

        return lockerRepository.findLockersBySectionId(sectionId).stream()
            .map(locker -> LockerAvailability.of(
                locker,
                appliedLockerIds.contains(locker.getId()),
                locker.getId().equals(myLockerId)
            ))
            .toList();
    }

    /** 내 사물함이 이 구역에 있는지. 회차당 신청이 최대 한 건이라 참이 되는 구역도 하나뿐이다. */
    private boolean containsMine(List<Locker> lockers, Long myLockerId) {
        return myLockerId != null && lockers.stream().anyMatch(locker -> myLockerId.equals(locker.getId()));
    }

    /** 아직 게시하지 않은 회차는 학생에게 없는 것으로 보여야 하므로 두 조회의 입구에서 같은 기준으로 거른다. */
    private void requirePublishedPeriod(Long lockerPeriodId) {
        if (!lockerRepository.existsPublishedPeriod(lockerPeriodId)) {
            throw new BusinessException(LockerErrorCode.LOCKER_PERIOD_NOT_FOUND);
        }
    }

    private void requireSection(Long sectionId) {
        if (!lockerRepository.existsSection(sectionId)) {
            throw new BusinessException(LockerErrorCode.LOCKER_SECTION_NOT_FOUND);
        }
    }
}
