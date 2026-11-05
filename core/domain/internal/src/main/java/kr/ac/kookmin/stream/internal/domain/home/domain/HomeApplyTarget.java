package kr.ac.kookmin.stream.internal.domain.home.domain;

/**
 * 홈 신청 섹션이 가리킬 수 있는 대상 하나. 운영진 후보 목록이 행사·사물함에서 모아 넘긴다.
 * <p>
 * {@code home_apply_sections}의 유니크 키 {@code (target_type, target_id)}와 같은 짝이라 설정 행을
 * 찾고 맞추는 열쇠로 쓴다.
 */
public record HomeApplyTarget(HomeApplyTargetType targetType, Long targetId) {
}
