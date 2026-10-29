package kr.ac.kookmin.stream.db;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * infrastructure:db 테스트(@DataJpaTest 등)가 찾아 쓰는 부트 설정. 이 모듈에는 실행 클래스가 없어서 테스트에만 둔다.
 * 엔티티·JPA 레포지토리 스캔이 이 패키지(kr.ac.kookmin.stream.db) 아래로 잡힌다.
 */
@SpringBootApplication
class DbTestApplication {
}
