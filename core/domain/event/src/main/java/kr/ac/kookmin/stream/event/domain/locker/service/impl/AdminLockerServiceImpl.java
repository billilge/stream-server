package kr.ac.kookmin.stream.event.domain.locker.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.OutboxWriter;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.common.event.LockerAssignedByAdmin;
import kr.ac.kookmin.stream.common.event.LockerAssignmentCanceled;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplication;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationResult;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationStatus;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodCreateCommand;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodSummary;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodUpdateCommand;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionLayout;
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
    private final OutboxWriter outboxWriter;

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
        List<Locker> lockers = getLockersInSection(sectionId, lockerIds);
        if (!status.isUsable()) {
            validateNotInUse(lockerIds);
        }

        lockers.forEach(locker -> locker.changeStatus(status));
        lockerRepository.saveLockers(lockers);
    }

    @Override
    @Transactional
    public void changeSectionLayout(Long sectionId, String layout) {
        validateSectionExists(sectionId);

        // 배치를 아직 등록하지 않은 구역이면 이 저장이 첫 등록이다
        LockerSectionLayout sectionLayout = lockerRepository.findLayoutBySectionId(sectionId)
            .orElseGet(() -> LockerSectionLayout.createEmpty(sectionId));
        sectionLayout.changeLayout(layout);

        lockerRepository.saveLayout(sectionLayout);
    }

    @Override
    @Transactional
    public void changeSectionPhotos(Long sectionId, List<Long> photoFileIds) {
        validateSectionExists(sectionId);

        // 사진은 배치와 같은 행에 있고 배치 쪽이 비어 있을 수 없다. 배치부터 등록해야 사진을 올릴 수 있다
        LockerSectionLayout sectionLayout = lockerRepository.findLayoutBySectionId(sectionId)
            .orElseThrow(() -> new BusinessException(LockerErrorCode.LOCKER_SECTION_LAYOUT_NOT_FOUND));
        sectionLayout.changePhotos(photoFileIds);

        lockerRepository.saveLayout(sectionLayout);
    }

    @Override
    public PageResult<LockerApplicationResult> getApplications(
        Long lockerPeriodId, List<Long> memberIds, PageOffset pageOffset
    ) {
        LockerPeriod period = getPeriod(lockerPeriodId);
        PageResult<LockerApplication> applications =
            lockerApplicationRepository.search(lockerPeriodId, memberIds, pageOffset);

        List<Long> lockerIds = applications.content().stream()
            .map(LockerApplication::getLockerId)
            .distinct()
            .toList();
        // 철거된 사물함도 지난 신청의 이름을 보여줘야 하므로 삭제된 사물함까지 읽는다
        Map<Long, Locker> lockers = lockerIds.isEmpty()
            ? Map.of()
            : lockerRepository.findLockersByIdsIncludingDeleted(lockerIds).stream()
                .collect(Collectors.toMap(Locker::getId, Function.identity()));

        return applications.map(
            application -> new LockerApplicationResult(application, lockers.get(application.getLockerId()), period));
    }

    @Override
    @Transactional
    public void cancelApplication(Long lockerApplicationId) {
        LockerApplication application = lockerApplicationRepository.findById(lockerApplicationId)
            .orElseThrow(() -> new BusinessException(LockerErrorCode.LOCKER_APPLICATION_NOT_FOUND));
        validateUsageNotEnded(application.getLockerPeriodId(), LocalDate.now());

        application.cancel();
        lockerApplicationRepository.save(application);
        outboxWriter.record(new LockerAssignmentCanceled(application.getMemberId(), application.getLockerPeriodId()));
    }

    @Override
    @Transactional
    public void assignLocker(Long lockerPeriodId, Long memberId, Long lockerId) {
        // 사용 기간 판정과 신청 시각이 같은 시계를 보게 한 번만 읽는다. 따로 읽으면 자정을 걸칠 때 기간 안으로
        // 통과한 신청이 다음 날짜로 기록된다
        LocalDateTime now = LocalDateTime.now();
        validateUsageNotEnded(lockerPeriodId, now.toLocalDate());
        Locker locker = lockerRepository.findLockerById(lockerId)
            .orElseThrow(() -> new BusinessException(LockerErrorCode.LOCKER_NOT_IN_SECTION));
        if (!locker.isSelectable(lockerApplicationRepository.existsByLocker(lockerPeriodId, lockerId))) {
            throw new BusinessException(LockerErrorCode.LOCKER_ALREADY_ASSIGNED);
        }

        // 회원이 이미 가진 칸이 있으면 저장 시 유니크 제약이 막는다
        lockerApplicationRepository.save(LockerApplication.create(lockerPeriodId, memberId, lockerId, now));
        outboxWriter.record(new LockerAssignedByAdmin(memberId, lockerPeriodId));
    }

    /**
     * 사용 기간이 끝난 회차는 신청을 손댈 수 없다. 취소해도 되돌릴 자리가 없고, 배정해도 쓸 수 없다.
     */
    private void validateUsageNotEnded(Long lockerPeriodId, LocalDate today) {
        if (getPeriod(lockerPeriodId).isUsageEnded(today)) {
            throw new BusinessException(LockerErrorCode.LOCKER_PERIOD_ALREADY_ENDED);
        }
    }

    /**
     * 요청한 사물함을 구역 소속으로 확인하고 돌려준다. 하나라도 그 구역에 없으면 아무것도 바꾸지 않도록 여기서 끊는다.
     */
    private List<Locker> getLockersInSection(Long sectionId, List<Long> lockerIds) {
        validateSectionExists(sectionId);

        // 구역 전체가 아니라 요청한 사물함만 읽는다. 36칸 구역에서 두 칸만 바꿔도 전체를 읽지 않도록 한다
        List<Locker> lockers = lockerRepository.findLockersByIds(lockerIds);
        boolean allInSection = lockers.size() == lockerIds.size()
            && lockers.stream().allMatch(locker -> locker.getSectionId().equals(sectionId));
        if (!allInSection) {
            // 없는 사물함과 다른 구역 사물함을 가르지 않는다. 운영 화면에서는 둘 다 "잘못 고른 칸"이다
            throw new BusinessException(LockerErrorCode.LOCKER_NOT_IN_SECTION);
        }
        return lockers;
    }

    /**
     * 아직 배정 상태인 신청이 걸린 사물함이 하나라도 있으면 끊는다.
     * <p>
     * 배정인지 만료인지는 내 신청 내역 화면과 같은 기준을 써야 하므로 {@link LockerApplicationStatus}에 맡긴다.
     * 저장소는 판정에 끼지 않고 회차를 읽고 신청 존재만 알려준다.
     */
    private void validateNotInUse(List<Long> lockerIds) {
        LocalDate today = LocalDate.now();
        // 회차를 먼저 좁힌다. 회차는 학기당 한두 건이라 전부 읽어도 되고, 그래야 신청 조회가 (회차, 사물함)
        // 유니크 인덱스를 탄다. 사물함 식별자만으로 신청을 찾으면 탈 인덱스가 없어 이력 전체를 훑는다.
        // 게시 여부로 거르지 않는다. 신청은 게시된 회차에만 들어와 지금은 차이가 없고, 거르려면 포트만 하나 더 는다
        Set<Long> assignedPeriodIds = lockerRepository.findAllPeriods().stream()
            .filter(period -> LockerApplicationStatus.from(period, today) == LockerApplicationStatus.ASSIGNED)
            .map(LockerPeriod::getId)
            .collect(Collectors.toSet());

        if (lockerApplicationRepository.existsByPeriodIdsAndLockerIds(assignedPeriodIds, lockerIds)) {
            throw new BusinessException(LockerErrorCode.LOCKER_IN_USE);
        }
    }

    /**
     * 없는 구역이면 같은 경로의 구역 상세 조회와 같이 404로 나가야 한다. 구역을 먼저 확인해 아무것도 바꾸지 않고 끊는다.
     */
    private void validateSectionExists(Long sectionId) {
        if (lockerRepository.findSectionById(sectionId).isEmpty()) {
            throw new BusinessException(LockerErrorCode.LOCKER_SECTION_NOT_FOUND);
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
