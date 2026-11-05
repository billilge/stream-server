package kr.ac.kookmin.stream.internal.domain.home.domain;

/**
 * 홈 신청 섹션이 가리키는 대상. 행사는 행사 하나, 사물함은 운영 회차 하나다.
 * <p>
 * 상수 이름이 {@code home_apply_sections.target_type}에 저장되므로 이름을 바꾸면 쌓인 설정 행을 읽지 못한다.
 */
public enum HomeApplyTargetType {

    EVENT,
    LOCKER
}
