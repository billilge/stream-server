package kr.ac.kookmin.stream.internal.domain.home.domain;

import java.time.LocalDateTime;

/**
 * 배너 수정 입력값. 입력값 전체를 받아 통째로 바꾸고, 표시 순서는 손대지 않는다 —
 * 순서는 목록에서 드래그로 바꾸는 별도 경로가 있다.
 *
 * @param targetId 공지·행사 식별자. 외부 링크면 읽지 않는다
 * @param linkUrl  외부 링크 주소. 공지·행사면 읽지 않는다
 */
public record HomeBannerUpdateCommand(
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
