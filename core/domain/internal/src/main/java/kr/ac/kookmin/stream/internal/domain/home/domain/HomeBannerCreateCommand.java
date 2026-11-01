package kr.ac.kookmin.stream.internal.domain.home.domain;

import java.time.LocalDateTime;

/**
 * 배너 등록 입력값. 표시 순서는 서버가 맨 뒤로 정하므로 받지 않는다.
 *
 * @param targetId 공지·행사 식별자. 외부 링크면 읽지 않는다
 * @param linkUrl  외부 링크 주소. 공지·행사면 읽지 않는다
 */
public record HomeBannerCreateCommand(
    String title,
    String subtitle,
    HomeBannerType bannerType,
    Long imageFileId,
    HomeBannerLandingType landingType,
    Long targetId,
    String linkUrl,
    LocalDateTime exposureStartAt,
    LocalDateTime exposureEndAt
) {
}
