package kr.ac.kookmin.stream.internal.domain.home.domain;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.common.BusinessException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 홈 상단 배너 한 건. 노출 기간 안의 배너를 {@code displayOrder} 순으로 보여준다.
 * <p>
 * 랜딩은 {@link HomeBannerLandingType}이 {@code targetId}와 {@code linkUrl} 중 어느 쪽을 읽을지 정한다.
 * 짝이 맞는지는 DB가 지키지 못하므로 {@link #create}·{@link #update}가 막고, 그전에 손으로 넣은 행은
 * 읽는 쪽이 {@link #hasValidLanding()}으로 거른다.
 * <p>
 * {@link #of}는 DB에서 복원하는 길이라 검증하지 않는다. 어긋난 행이 있어도 조회가 깨지지 않아야 한다.
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HomeBanner {

    private Long id;
    private String title;
    private String subtitle;
    private HomeBannerCategory category;
    private Long imageFileId;
    private HomeBannerLandingType landingType;
    private Long targetId;
    private String linkUrl;
    private int displayOrder;
    private LocalDateTime exposureStartAt;
    private LocalDateTime exposureEndAt;
    /** 등록 일시. 운영진 상세가 보여준다 */
    private LocalDateTime createdAt;

    public static HomeBanner of(
        Long id,
        String title,
        String subtitle,
        HomeBannerCategory category,
        Long imageFileId,
        HomeBannerLandingType landingType,
        Long targetId,
        String linkUrl,
        int displayOrder,
        LocalDateTime exposureStartAt,
        LocalDateTime exposureEndAt,
        LocalDateTime createdAt
    ) {
        return new HomeBanner(
            id, title, subtitle, category, imageFileId, landingType, targetId, linkUrl,
            displayOrder, exposureStartAt, exposureEndAt, createdAt
        );
    }

    /**
     * 새 배너. 등록 일시는 DB가 채운다.
     *
     * @param displayOrder 표시 순서. 맨 뒤에 붙이는 값을 서비스가 정해 넘긴다
     */
    public static HomeBanner create(HomeBannerCreateCommand command, int displayOrder) {
        validateExposurePeriod(command.exposureStartAt(), command.exposureEndAt());
        validateLanding(command.landingType(), command.targetId(), command.linkUrl());

        return new HomeBanner(
            null,
            command.title(),
            command.subtitle(),
            command.category(),
            command.imageFileId(),
            command.landingType(),
            normalizeTargetId(command.landingType(), command.targetId()),
            normalizeLinkUrl(command.landingType(), command.linkUrl()),
            displayOrder,
            command.exposureStartAt(),
            command.exposureEndAt(),
            null
        );
    }

    /**
     * 입력값을 통째로 바꾼다. 표시 순서는 그대로 둔다.
     */
    public void update(HomeBannerUpdateCommand command) {
        validateExposurePeriod(command.exposureStartAt(), command.exposureEndAt());
        validateLanding(command.landingType(), command.targetId(), command.linkUrl());

        this.title = command.title();
        this.subtitle = command.subtitle();
        this.category = command.category();
        this.imageFileId = command.imageFileId();
        this.landingType = command.landingType();
        this.targetId = normalizeTargetId(command.landingType(), command.targetId());
        this.linkUrl = normalizeLinkUrl(command.landingType(), command.linkUrl());
        this.exposureStartAt = command.exposureStartAt();
        this.exposureEndAt = command.exposureEndAt();
    }

    public void changeDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    /**
     * 노출 상태. 저장값이 아니라 노출 기간과 {@code now}로 판정한다.
     */
    public HomeBannerStatus calculateStatus(LocalDateTime now) {
        if (now.isBefore(exposureStartAt)) {
            return HomeBannerStatus.SCHEDULED;
        }
        if (now.isAfter(exposureEndAt)) {
            return HomeBannerStatus.ENDED;
        }
        return HomeBannerStatus.EXPOSED;
    }

    /**
     * 운영진이 직접 올린 배경 파일. 프리셋 카테고리는 서버가 채운 값이라 운영진이 고른 적이 없으므로
     * {@code null}이다.
     * <p>
     * 수정 화면이 이 값을 그대로 되돌려 보내는 것을 전제로 한다. 프리셋 파일까지 내려주면 카테고리만
     * {@code ETC}로 바꿔 저장할 때 그 프리셋이 따라와, 운영진이 고르지 않은 배경이 기타 배너에 붙는다.
     */
    public Long getUploadedImageFileId() {
        return category == HomeBannerCategory.ETC ? imageFileId : null;
    }

    /**
     * 랜딩 타입과 값의 짝이 맞는지. 쓰기 API가 없는 동안 DB에 손으로 넣은 행이 어긋날 수 있어,
     * 어긋난 배너는 홈에서 빼고 나머지는 그대로 보여준다 — 한 건 때문에 홈 전체가 막히지 않게 한다.
     */
    public boolean hasValidLanding() {
        return isValidLanding(landingType, targetId, linkUrl);
    }

    // switch 식으로 모든 랜딩 타입을 받는다. 값이 늘면 컴파일이 막으므로 새 타입이 "targetId만 보면 된다"로
    // 조용히 흘러가지 않는다
    private static boolean isValidLanding(HomeBannerLandingType landingType, Long targetId, String linkUrl) {
        return switch (landingType) {
            case NOTICE, EVENT -> targetId != null;
            case EXTERNAL_LINK -> linkUrl != null && !linkUrl.isBlank();
        };
    }

    private static void validateLanding(HomeBannerLandingType landingType, Long targetId, String linkUrl) {
        if (!isValidLanding(landingType, targetId, linkUrl)) {
            throw new BusinessException(HomeErrorCode.HOME_BANNER_INVALID_LANDING);
        }
    }

    private static void validateExposurePeriod(LocalDateTime startAt, LocalDateTime endAt) {
        if (!endAt.isAfter(startAt)) {
            throw new BusinessException(HomeErrorCode.HOME_BANNER_INVALID_EXPOSURE_PERIOD);
        }
    }

    // 랜딩 타입이 읽지 않는 쪽은 비워 저장한다. 외부 링크였던 배너를 공지로 바꿀 때 예전 주소를 남겨두면
    // 수정 화면이 쓰지도 않는 값을 다시 채워 보여준다.
    //
    // isValidLanding과 같은 이유로 switch 식으로 받는다. 랜딩 타입이 늘면 컴파일이 막으므로 새 타입이
    // 어느 쪽을 쓰는지 정하지 않은 채로 지나가지 않는다
    private static Long normalizeTargetId(HomeBannerLandingType landingType, Long targetId) {
        return switch (landingType) {
            case NOTICE, EVENT -> targetId;
            case EXTERNAL_LINK -> null;
        };
    }

    private static String normalizeLinkUrl(HomeBannerLandingType landingType, String linkUrl) {
        return switch (landingType) {
            case NOTICE, EVENT -> null;
            case EXTERNAL_LINK -> linkUrl;
        };
    }
}
