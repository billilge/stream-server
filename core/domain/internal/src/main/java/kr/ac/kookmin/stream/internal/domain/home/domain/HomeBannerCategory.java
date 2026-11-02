package kr.ac.kookmin.stream.internal.domain.home.domain;

/**
 * 배너 카테고리. 운영진이 등록 화면에서 고르는 값이고, 배경 이미지가 이 값으로 정해진다.
 * <p>
 * {@link #ETC}를 뺀 나머지는 카테고리마다 배경이 하나로 정해져 있어 운영진이 이미지를 고르지 않는다. 어느 파일이
 * 그 배경인지는 환경마다 달라 코드가 아니라 설정값({@code admin_config_values})에 두고, 등록·수정 때 서버가
 * 꺼내 채운다. {@code ETC}만 운영진이 직접 올린 이미지를 쓴다.
 * <p>
 * 운영진 목록의 칩과 필터도 이 값으로 그린다. 학생 앱은 읽지 않는다 — 앱에는 정해진 배경이 이미지 URL로 내려간다.
 * <p>
 * 상수 이름이 {@code home_banners.category}에 저장되므로 이름을 바꾸면 쌓인 배너를 읽지 못한다.
 * <p>
 * 랜딩 타입과는 다른 축이다. 제휴 배너가 공지로 갈 수도, 외부 링크로 갈 수도 있다.
 */
public enum HomeBannerCategory {

    /** 일반 공지 */
    GENERAL_NOTICE,
    /** 행사 */
    EVENT,
    /** 간식 행사 */
    SNACK_EVENT,
    /** 제휴 안내 */
    PARTNERSHIP,
    /** 사물함 */
    LOCKER,
    /** 기타. 정해진 배경이 없어 운영진이 직접 올린 이미지를 쓴다 */
    ETC
}
