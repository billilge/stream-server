package kr.ac.kookmin.stream;

/**
 * 인증된 사용자의 스냅샷. 각 클라이언트 모듈이 role별 구현체를 두고 ArgumentResolver가 컨트롤러 파라미터로 주입한다.
 */
public interface ApiUser {

    Long userId();
}
