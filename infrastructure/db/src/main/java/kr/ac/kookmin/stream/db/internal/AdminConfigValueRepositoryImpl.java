package kr.ac.kookmin.stream.db.internal;

import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.config.domain.AdminConfigValue;
import kr.ac.kookmin.stream.internal.domain.config.repository.AdminConfigValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminConfigValueRepositoryImpl implements AdminConfigValueRepository {

    private final AdminConfigValueJpaRepository adminConfigValueJpaRepository;

    @Override
    public Optional<AdminConfigValue> findByKey(String configKey) {
        return adminConfigValueJpaRepository.findByConfigKey(configKey).map(AdminConfigValueJpaEntity::toDomain);
    }

    @Override
    public void upsert(String configKey, String configValue) {
        adminConfigValueJpaRepository.upsert(configKey, configValue);
    }
}
