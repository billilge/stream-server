package kr.ac.kookmin.stream.internal.domain.home.domain;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 홈 상단 배너 한 건. 노출 기간 안의 배너를 {@code displayOrder} 순으로 보여준다.
 * <p>
 * 랜딩은 {@link HomeBannerLandingType}이 {@code targetId}와 {@code linkUrl} 중 어느 쪽을 읽을지 정한다.
 * 짝이 맞는지는 DB가 지키지 못하므로 {@link #hasValidLanding()}으로 읽는 쪽이 확인한다.
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HomeBanner {

    private Long id;
    private String title;
    private String subtitle;
    private Long imageFileId;
    private HomeBannerLandingType landingType;
    private Long targetId;
    private String linkUrl;
    private int displayOrder;
    private LocalDateTime exposureStartAt;
    private LocalDateTime exposureEndAt;

    public static HomeBanner of(
        Long id,
        String title,
        String subtitle,
        Long imageFileId,
        HomeBannerLandingType landingType,
        Long targetId,
        String linkUrl,
        int displayOrder,
        LocalDateTime exposureStartAt,
        LocalDateTime exposureEndAt
    ) {
        return new HomeBanner(
            id, title, subtitle, imageFileId, landingType, targetId, linkUrl,
            displayOrder, exposureStartAt, exposureEndAt
        );
    }

    /**
     * 랜딩 타입과 값의 짝이 맞는지. 관리자 쓰기 API가 없는 동안 DB에 손으로 넣은 행이 어긋날 수 있어,
     * 어긋난 배너는 홈에서 빼고 나머지는 그대로 보여준다 — 한 건 때문에 홈 전체가 막히지 않게 한다.
     */
    public boolean hasValidLanding() {
        if (landingType == HomeBannerLandingType.EXTERNAL_LINK) {
            return linkUrl != null && !linkUrl.isBlank();
        }
        return targetId != null;
    }

    public boolean isExposedAt(LocalDateTime at) {
        return !at.isBefore(exposureStartAt) && !at.isAfter(exposureEndAt);
    }
}
