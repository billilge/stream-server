package kr.ac.kookmin.stream.internal.domain.home.domain;

import java.time.LocalDateTime;

/**
 * 배너 등록 입력값. 표시 순서는 서버가 맨 뒤로 정하므로 받지 않는다.
 *
 * @param imageFileId    배경 이미지 파일. {@code ETC}가 아니면 카테고리의 프리셋으로 채워져 들어온다
 * @param targetId       공지·행사 식별자. 외부 링크면 읽지 않는다
 * @param linkUrl        외부 링크 주소. 공지·행사면 읽지 않는다
 */
public record HomeBannerCreateCommand(
    String title,
    String subtitle,
    HomeBannerCategory category,
    Long imageFileId,
    HomeBannerLandingType landingType,
    Long targetId,
    String linkUrl,
    LocalDateTime exposureStartAt,
    LocalDateTime exposureEndAt
) {

    /**
     * 배경 이미지만 바꾼 사본. 요청은 {@code ETC}일 때만 파일을 담아 오므로, 카테고리로 프리셋을 찾아낸
     * 쪽이 이 메서드로 채워 넣는다.
     */
    public HomeBannerCreateCommand withImageFileId(Long imageFileId) {
        return new HomeBannerCreateCommand(
            title, subtitle, category, imageFileId, landingType, targetId, linkUrl,
            exposureStartAt, exposureEndAt
        );
    }
}
