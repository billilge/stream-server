package kr.ac.kookmin.stream.db.internal;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminConfigValueJpaRepository extends JpaRepository<AdminConfigValueJpaEntity, Long> {
    Optional<AdminConfigValueJpaEntity> findByConfigKey(String configKey);

    // config_key 유니크 제약을 이용한 원자적 upsert. JPQL로는 표현할 수 없는 MySQL 전용
    // 문법(ON DUPLICATE KEY UPDATE)이라 부득이하게 nativeQuery를 쓴다.
    @Modifying(clearAutomatically = true)
    @Query(
        value = "INSERT INTO admin_config_values (config_key, config_value) VALUES (:configKey, :configValue) "
            + "ON DUPLICATE KEY UPDATE config_value = VALUES(config_value)",
        nativeQuery = true
    )
    void upsert(@Param("configKey") String configKey, @Param("configValue") String configValue);
}
