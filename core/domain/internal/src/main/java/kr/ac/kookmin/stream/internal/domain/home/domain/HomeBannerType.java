package kr.ac.kookmin.stream.internal.domain.home.domain;

/**
 * 배너 분류. 운영진 목록의 칩과 필터에만 쓴다.
 * <p>
 * 이미지와는 무관하다 — 어떤 이미지를 쓸지는 {@code imageFileId}가 혼자 정하고, 프리셋도 직접 업로드와
 * 같이 {@code files}에 올려 식별자로 가리킨다.
 * <p>
 * 랜딩 타입과도 다른 축이다. 제휴 배너가 공지로 갈 수도, 외부 링크로 갈 수도 있다.
 */
public enum HomeBannerType {

    /** 제휴 안내 */
    PARTNERSHIP,
    /** 그 밖의 전부 */
    GENERAL
}
