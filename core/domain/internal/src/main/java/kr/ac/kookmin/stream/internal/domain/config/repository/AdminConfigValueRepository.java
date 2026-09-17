package kr.ac.kookmin.stream.internal.domain.config.repository;

import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.config.domain.AdminConfigValue;

public interface AdminConfigValueRepository {
    Optional<AdminConfigValue> findByKey(String configKey);
    AdminConfigValue save(AdminConfigValue configValue);
}
