package kr.ac.kookmin.stream.internal.domain.config.service.impl;

import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.config.domain.AdminConfigValue;
import kr.ac.kookmin.stream.internal.domain.config.repository.ConfigRepository;
import kr.ac.kookmin.stream.internal.domain.config.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class ConfigServiceImpl implements ConfigService {

    private final ConfigRepository configRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getValue(String configKey) {
        return configRepository.findByKey(configKey).map(AdminConfigValue::getConfigValue);
    }

    @Override
    @Transactional
    public AdminConfigValue upsertValue(String configKey, String configValue) {
        AdminConfigValue value = configRepository.findByKey(configKey)
            .map(existing -> existing.withValue(configValue))
            .orElseGet(() -> AdminConfigValue.create(configKey, configValue));
        return configRepository.save(value);
    }
}
