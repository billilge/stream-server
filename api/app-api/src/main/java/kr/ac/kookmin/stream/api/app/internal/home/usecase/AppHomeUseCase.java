package kr.ac.kookmin.stream.api.app.internal.home.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.api.app.internal.home.response.HomeLockerStatus;
import kr.ac.kookmin.stream.api.app.internal.home.response.HomeResponse;
import kr.ac.kookmin.stream.api.app.internal.home.response.HomeStudentFeeStatus;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.api.common.dto.PreviewResponse;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.service.EventApplicationService;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationResult;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import kr.ac.kookmin.stream.event.domain.locker.service.LockerApplicationService;
import kr.ac.kookmin.stream.event.domain.locker.service.LockerService;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.file.service.FileService;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplySection;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTargetType;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;
import kr.ac.kookmin.stream.internal.domain.home.service.HomeService;
import kr.ac.kookmin.stream.welfare.domain.feedback.service.FeedbackService;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.service.StudentTransferStatusService;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.service.RentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 홈 화면 한 번에 그릴 데이터를 모은다. 여섯 도메인을 가로지르므로 서비스끼리 부르지 않고 여기서 조합한다
 * ({@code architecture.md} 6-1절).
 * <p>
 * 섹션 하나가 비어도 나머지는 그대로 내려간다 — 홈이 통째로 막히지 않게 한다.
 */
@Component
@RequiredArgsConstructor
public class AppHomeUseCase {

    /** 미리보기로 보여줄 건수. 나머지는 전체 수만 주고 "더보기"로 각 화면에 넘긴다. */
    private static final int PREVIEW_LIMIT = 2;

    private final HomeService homeService;
    private final FileService fileService;
    private final EventService eventService;
    private final EventApplicationService eventApplicationService;
    private final LockerService lockerService;
    private final LockerApplicationService lockerApplicationService;
    private final RentalHistoryService rentalHistoryService;
    private final FeedbackService feedbackService;
    private final StudentTransferStatusService studentTransferStatusService;

    public HomeResponse getHome(Long memberId) {
        // 사물함 신청 내역은 배정 상태와 신청 섹션의 신청 여부가 함께 쓰므로 한 번만 읽는다
        List<LockerApplicationResult> lockerApplications =
            lockerApplicationService.getApplicationsByMemberId(memberId);
        LocalDateTime now = LocalDateTime.now();

        return new HomeResponse(
            toBanners(),
            toApplySections(memberId, lockerApplications),
            toRentals(memberId, now),
            toAppliedEvents(memberId),
            new HomeResponse.StudentFee(toFeeStatus(memberId)),
            new HomeResponse.OpenFeedback(feedbackService.countByMemberId(memberId)),
            toLocker(lockerApplications, now.toLocalDate())
        );
    }

    private List<HomeResponse.Banner> toBanners() {
        List<HomeBanner> banners = homeService.getExposedBanners();

        // 이미지는 식별자를 모아 한 번에 읽는다. 배너마다 따로 읽으면 배너 수만큼 쿼리가 더 나간다
        Map<Long, File> files = fileService.findAllByIdIn(
            banners.stream().map(HomeBanner::getImageFileId).toList());

        return banners.stream()
            .map(banner -> new HomeResponse.Banner(
                banner.getId(),
                banner.getTitle(),
                banner.getSubtitle(),
                StorageUrlBuilder.build(banner.getImageFileId(), files),
                banner.getLandingType(),
                banner.getTargetId(),
                banner.getLinkUrl()
            ))
            .toList();
    }

    /**
     * 노출로 설정된 섹션 중 지금 신청할 수 있는 것만 설정 순서대로 남긴다. 마감되거나 미게시된 대상은
     * 누를 수 없는 "신청하기"가 되므로 뺀다.
     */
    private List<HomeResponse.ApplySection> toApplySections(
        Long memberId,
        List<LockerApplicationResult> lockerApplications
    ) {
        List<HomeApplySection> sections = homeService.getExposedApplySections();
        List<Long> eventIds = targetIdsOf(sections, HomeApplyTargetType.EVENT);
        List<Long> periodIds = targetIdsOf(sections, HomeApplyTargetType.LOCKER);

        Map<Long, Event> events = eventService.getApplicableEvents(eventIds).stream()
            .collect(Collectors.toMap(Event::getId, Function.identity()));
        Map<Long, LockerPeriod> periods = lockerService.getApplicablePeriods(periodIds).stream()
            .collect(Collectors.toMap(LockerPeriod::getId, Function.identity()));

        Set<Long> appliedEventIds = eventApplicationService.getAppliedEventIds(memberId, eventIds);
        Set<Long> appliedPeriodIds = lockerApplications.stream()
            .map(application -> application.period().getId())
            .collect(Collectors.toSet());

        return sections.stream()
            .map(section -> toApplySection(section, events, periods, appliedEventIds, appliedPeriodIds))
            .filter(Objects::nonNull)
            .toList();
    }

    private HomeResponse.ApplySection toApplySection(
        HomeApplySection section,
        Map<Long, Event> events,
        Map<Long, LockerPeriod> periods,
        Set<Long> appliedEventIds,
        Set<Long> appliedPeriodIds
    ) {
        Long targetId = section.getTargetId();
        if (section.getTargetType() == HomeApplyTargetType.EVENT) {
            Event event = events.get(targetId);
            return event == null ? null : new HomeResponse.ApplySection(
                HomeApplyTargetType.EVENT,
                targetId,
                event.getTitle(),
                event.getApplyEndAt(),
                appliedEventIds.contains(targetId)
            );
        }

        LockerPeriod period = periods.get(targetId);
        return period == null ? null : new HomeResponse.ApplySection(
            HomeApplyTargetType.LOCKER,
            targetId,
            period.getName(),
            period.getApplyEndAt(),
            appliedPeriodIds.contains(targetId)
        );
    }

    private static List<Long> targetIdsOf(List<HomeApplySection> sections, HomeApplyTargetType type) {
        return sections.stream()
            .filter(section -> section.getTargetType() == type)
            .map(HomeApplySection::getTargetId)
            .toList();
    }

    private PreviewResponse<HomeResponse.Rental> toRentals(Long memberId, LocalDateTime now) {
        return PreviewResponse.from(
            rentalHistoryService.getActiveRentals(memberId, PREVIEW_LIMIT),
            record -> new HomeResponse.Rental(
                record.history().getId(),
                record.itemName(),
                StorageUrlBuilder.build(record.itemImageKey()),
                record.dueAt(),
                record.dueAt().isBefore(now),
                record.isReturnRequested()
            ));
    }

    private PreviewResponse<HomeResponse.AppliedEvent> toAppliedEvents(Long memberId) {
        return PreviewResponse.from(
            eventApplicationService.getUpcomingAppliedEvents(memberId, PREVIEW_LIMIT),
            event -> new HomeResponse.AppliedEvent(
                event.getId(),
                event.getTitle(),
                event.getEventStartAt().toLocalDate()
            ));
    }

    private HomeStudentFeeStatus toFeeStatus(Long memberId) {
        return HomeStudentFeeStatus.from(studentTransferStatusService.findLatestByMemberId(memberId)
            .map(StudentTransferStatus::getStatus)
            .orElse(null));
    }

    /**
     * 지금 쓰는 사물함이 있으면 그것을, 없으면 지난 배정이 있었는지만 알려준다. 회차는 기간이 겹치지 않아
     * 사용 중인 배정은 많아야 하나다.
     */
    private HomeResponse.Locker toLocker(List<LockerApplicationResult> applications, LocalDate today) {
        return applications.stream()
            .filter(application -> !application.period().isUsageEnded(today))
            .findFirst()
            .map(application -> new HomeResponse.Locker(
                HomeLockerStatus.ASSIGNED, application.locker().getLockerLabel()))
            .orElseGet(() -> new HomeResponse.Locker(
                applications.isEmpty() ? HomeLockerStatus.NOT_APPLIED : HomeLockerStatus.EXPIRED,
                null
            ));
    }
}
