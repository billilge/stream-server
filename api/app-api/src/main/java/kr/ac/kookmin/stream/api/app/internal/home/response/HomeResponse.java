package kr.ac.kookmin.stream.api.app.internal.home.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.api.common.dto.PreviewResponse;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTargetType;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerLandingType;

/**
 * 홈 화면 한 번에 그릴 데이터. 섹션이 비어도 필드는 빠지지 않는다 — 화면이 빈 상태 UI로 떨어진다.
 */
public record HomeResponse(
    List<Banner> banners,
    List<ApplySection> applySections,
    PreviewResponse<Rental> rentals,
    PreviewResponse<AppliedEvent> appliedEvents,
    StudentFee studentFee,
    OpenFeedback openFeedback,
    Locker locker
) {

    /**
     * @param targetId  {@code landingType}이 가리키는 대상의 식별자. {@code EXTERNAL_LINK}면 비어 있다
     * @param linkUrl   외부 링크 주소. {@code EXTERNAL_LINK}일 때만 채워진다
     */
    public record Banner(
        Long bannerId,
        String title,
        String subtitle,
        String imageUrl,
        HomeBannerLandingType landingType,
        Long targetId,
        String linkUrl
    ) {
    }

    /**
     * @param applyEndAt 신청 마감 일시. 화면이 D-day를 센다
     */
    public record ApplySection(
        HomeApplyTargetType targetType,
        Long targetId,
        String title,
        LocalDateTime applyEndAt,
        boolean isApplied
    ) {
    }

    public record Rental(
        Long rentalId,
        String itemName,
        String itemImageUrl,
        LocalDateTime dueAt,
        boolean isOverdue,
        boolean isReturnRequested
    ) {
    }

    /**
     * @param eventDate 행사일. 화면이 D-day를 세므로 날짜만 준다
     */
    public record AppliedEvent(
        Long eventId,
        String title,
        LocalDate eventDate
    ) {
    }

    public record StudentFee(HomeStudentFeeStatus status) {
    }

    /**
     * @param writtenCount 작성한 질문 수. 0이면 화면이 "-"로 그린다
     */
    public record OpenFeedback(long writtenCount) {
    }

    /**
     * @param lockerNumber 배정된 사물함 이름. {@code ASSIGNED}일 때만 채워진다
     */
    public record Locker(HomeLockerStatus status, String lockerNumber) {
    }
}
