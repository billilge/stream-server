package kr.ac.kookmin.stream.db.internal;

import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.config.domain.AdminConfigValue;
import kr.ac.kookmin.stream.internal.domain.config.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConfigRepositoryImpl implements ConfigRepository {

    private final AdminConfigValueJpaRepository adminConfigValueJpaRepository;

    @Override
    public Optional<AdminConfigValue> findByKey(String configKey) {
        return adminConfigValueJpaRepository.findByConfigKey(configKey).map(AdminConfigValueJpaEntity::toDomain);
    }

    @Override
    public AdminConfigValue save(AdminConfigValue configValue) {
        return adminConfigValueJpaRepository.save(AdminConfigValueJpaEntity.from(configValue)).toDomain();
    }
}
