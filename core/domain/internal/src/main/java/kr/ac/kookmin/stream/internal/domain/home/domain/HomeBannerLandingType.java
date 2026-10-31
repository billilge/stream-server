package kr.ac.kookmin.stream.internal.domain.home.domain;

/**
 * 배너를 눌렀을 때 가는 곳. 공지·행사는 앱 안 화면이라 식별자로, 외부 링크는 주소로 간다.
 */
public enum HomeBannerLandingType {

    NOTICE,
    EVENT,
    EXTERNAL_LINK
}
