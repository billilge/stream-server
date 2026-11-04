package kr.ac.kookmin.stream.api.app.internal.home.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
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
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationStatus;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import kr.ac.kookmin.stream.event.domain.locker.service.LockerApplicationService;
import kr.ac.kookmin.stream.event.domain.locker.service.LockerService;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.file.service.FileService;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplySection;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTargetType;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerLandingType;
import kr.ac.kookmin.stream.internal.domain.home.service.HomeService;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.service.StudentTransferStatusService;
import kr.ac.kookmin.stream.welfare.domain.feedback.service.FeedbackService;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.service.NoticeService;
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
    private final NoticeService noticeService;
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

    /**
     * 노출 기간 안의 배너 중 연결 대상이 지금 학생에게 보이는 것만 남긴다. 눌러도 공지·행사를 찾을 수 없는
     * 배너는 홈에 띄우지 않는다 — 신청 섹션에서 마감된 대상을 빼는 것과 같은 이유다.
     */
    private List<HomeResponse.Banner> toBanners() {
        List<HomeBanner> banners = homeService.getExposedBanners();

        // 이미지는 식별자를 모아 한 번에 읽는다. 배너마다 따로 읽으면 배너 수만큼 쿼리가 더 나간다
        Map<Long, File> files = fileService.findAllByIdIn(
            banners.stream().map(HomeBanner::getImageFileId).toList());

        // 연결 대상도 같은 이유로 랜딩 타입별로 모아 한 번에 읽는다. 둘 다 빈 목록이면 쿼리가 나가지 않는다
        Set<Long> noticeIds = noticeService.getByIds(
                landingTargetIdsOf(banners, HomeBannerLandingType.NOTICE)).stream()
            .map(Notice::getId)
            .collect(Collectors.toSet());
        Set<Long> publishedEventIds = eventService.getPublishedByIds(
                landingTargetIdsOf(banners, HomeBannerLandingType.EVENT)).stream()
            .map(Event::getId)
            .collect(Collectors.toSet());

        return banners.stream()
            .filter(banner -> hasLandingTarget(banner, noticeIds, publishedEventIds))
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
     * 연결 대상이 아직 학생에게 보이는지. 타입과 값의 짝은 {@link HomeBanner#hasValidLanding()}이 도메인에서
     * 보지만, 대상이 지워졌는지·아직 미게시인지는 홈 도메인이 공지·행사를 몰라서({@code internal}은
     * {@code welfare}·{@code event}를 의존하지 않는다) 여기서 본다.
     * <p>
     * 랜딩 타입을 switch 식으로 모두 받는다. 값이 늘면 컴파일이 막으므로 새 타입이 확인 없이 지나가지 않는다.
     */
    private static boolean hasLandingTarget(HomeBanner banner, Set<Long> noticeIds, Set<Long> publishedEventIds) {
        return switch (banner.getLandingType()) {
            case NOTICE -> noticeIds.contains(banner.getTargetId());
            case EVENT -> publishedEventIds.contains(banner.getTargetId());
            // 앱 밖으로 나가는 주소라 확인할 대상이 없다
            case EXTERNAL_LINK -> true;
        };
    }

    private static List<Long> landingTargetIdsOf(List<HomeBanner> banners, HomeBannerLandingType landingType) {
        return banners.stream()
            .filter(banner -> banner.getLandingType() == landingType)
            .map(HomeBanner::getTargetId)
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

    // 대상 종류를 switch 식으로 모두 받는다. 값이 늘면 컴파일이 막으므로 새 종류가 조용히 사물함으로 처리되지 않는다
    private HomeResponse.ApplySection toApplySection(
        HomeApplySection section,
        Map<Long, Event> events,
        Map<Long, LockerPeriod> periods,
        Set<Long> appliedEventIds,
        Set<Long> appliedPeriodIds
    ) {
        return switch (section.getTargetType()) {
            case EVENT -> toEventSection(section.getTargetId(), events, appliedEventIds);
            case LOCKER -> toLockerSection(section.getTargetId(), periods, appliedPeriodIds);
        };
    }

    /** 마감됐거나 미게시라 {@code events}에 없으면 누를 수 없는 섹션이므로 {@code null}로 빼 달라고 알린다. */
    private HomeResponse.ApplySection toEventSection(
        Long targetId,
        Map<Long, Event> events,
        Set<Long> appliedEventIds
    ) {
        Event event = events.get(targetId);
        return event == null ? null : new HomeResponse.ApplySection(
            HomeApplyTargetType.EVENT,
            targetId,
            event.getTitle(),
            event.getApplyEndAt(),
            appliedEventIds.contains(targetId)
        );
    }

    /** 신청 기간이 아니거나 미게시라 {@code periods}에 없으면 {@code null}로 빼 달라고 알린다. */
    private HomeResponse.ApplySection toLockerSection(
        Long targetId,
        Map<Long, LockerPeriod> periods,
        Set<Long> appliedPeriodIds
    ) {
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
            applied -> new HomeResponse.AppliedEvent(
                applied.application().getId(),
                applied.event().getId(),
                applied.event().getTitle(),
                applied.event().getEventStartAt().toLocalDate()
            ));
    }

    private HomeStudentFeeStatus toFeeStatus(Long memberId) {
        return HomeStudentFeeStatus.from(studentTransferStatusService.findLatestByMemberId(memberId)
            .map(StudentTransferStatus::getStatus)
            .orElse(null));
    }

    /**
     * 지금 쓰는 사물함이 있으면 그것을, 없으면 지난 배정이 있었는지만 알려준다.
     * <p>
     * 사용이 안 끝난 배정이 둘일 수 있다. 학생 신청은 신청 기간에만 생겨 회차가 겹치지 않지만, 운영진 배정
     * ({@code AdminLockerServiceImpl.assignLocker})은 사용 종료만 보므로 다음 회차 칸을 미리 줄 수 있다.
     * 그중 지금 쓰는 칸은 사용 시작이 가장 이른 것이다 — 신청 시각순으로 고르면 다음 학기 칸이 뜬다.
     * <p>
     * 배정·종료 판정은 "내 사물함" 화면과 같은 기준을 써야 하므로 {@link LockerApplicationStatus}에 맡긴다.
     */
    private HomeResponse.Locker toLocker(List<LockerApplicationResult> applications, LocalDate today) {
        if (applications.isEmpty()) {
            return new HomeResponse.Locker(HomeLockerStatus.NOT_APPLIED, null);
        }

        return applications.stream()
            .filter(application -> LockerApplicationStatus.from(application.period(), today)
                == LockerApplicationStatus.ASSIGNED)
            .min(Comparator.comparing(application -> application.period().getUsageStartAt()))
            .map(application -> new HomeResponse.Locker(
                HomeLockerStatus.from(LockerApplicationStatus.ASSIGNED),
                application.locker().getLockerLabel()))
            .orElseGet(() -> new HomeResponse.Locker(
                HomeLockerStatus.from(LockerApplicationStatus.EXPIRED), null));
    }
}
