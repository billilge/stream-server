package kr.ac.kookmin.stream.auth.domain.oauth.domain;

/**
 * provider에서 확인한 사용자 정보.
 * <p>
 * providerUserId는 provider 안에서 바뀌지 않는 고유 ID로, 회원을 찾는 키가 된다.
 * studentId·major·academicStatus는 학번·학과·학적을 주지 않는 provider(구글·카카오)에서 null일 수 있다.
 * academicStatus는 provider가 주는 학적 상태 값을 그대로 담는다.
 */
public record OAuthUserInfo(
    OAuthProvider provider,
    String providerUserId,
    String studentId,
    String name,
    String major,
    String academicStatus
) {}
