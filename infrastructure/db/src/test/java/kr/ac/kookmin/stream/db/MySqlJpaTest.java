package kr.ac.kookmin.stream.db;

import java.util.Map;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

/**
 * 실제 MySQL이 필요한 infrastructure:db 테스트의 부모 클래스. 상속하면 아래가 갖춰진다.
 * <ul>
 *   <li>MySQL 컨테이너를 이 모듈의 테스트 JVM에서 한 번만 띄우고 모든 테스트 클래스가 함께 쓴다(싱글턴 컨테이너).
 *       클래스마다 {@code @Container}로 띄우면 클래스 수만큼 MySQL이 뜨고 내려간다.
 *       컨테이너는 JVM이 끝날 때 Testcontainers(Ryuk)가 정리한다.</li>
 *   <li>Flyway 마이그레이션을 그대로 돌리고, {@code ddl-auto=validate}로 엔티티 매핑을 검증한다.</li>
 *   <li>Docker가 없으면 실패하지 않고 건너뛴다.</li>
 * </ul>
 * 클래스끼리 DB를 공유하므로, 테스트마다 자기 데이터를 새로 만들어 쓰고 다른 테스트가 남긴 데이터에 기대지 않는다.
 * 설정(애너테이션)을 바꾸지 않고 상속하면 Spring 테스트 컨텍스트도 캐시되어 다시 뜨지 않는다.
 */
@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
public abstract class MySqlJpaTest {

    // bootstrap의 MySqlIntegrationTest와 같은 설정이다. 바꿀 때 둘 다 바꾼다.
    // 버릴 테스트 데이터라 디스크 대신 메모리(tmpfs)에 두고, 장애 복구용 디스크 쓰기를 끈다.
    // 트랜잭션 격리·락 동작은 그대로라 낙관적 락·동시성 테스트 결과에는 영향이 없다.
    @ServiceConnection
    private static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.4")
        .withTmpFs(Map.of("/var/lib/mysql", "rw"))
        .withCommand(
            "--innodb-flush-log-at-trx-commit=0",   // 커밋마다 redo log를 디스크에 내리지 않는다
            "--innodb-doublewrite=OFF",             // 페이지 이중 쓰기를 끈다
            "--skip-log-bin",                        // 복제용 바이너리 로그를 끈다
            "--performance-schema=OFF"              // 성능 계측을 꺼 기동을 줄인다
        );

    static {
        MYSQL.start();
    }
}
