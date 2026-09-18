package kr.ac.kookmin.stream.internal.domain.config.service.impl;

import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.config.domain.AdminConfigValue;
import kr.ac.kookmin.stream.internal.domain.config.repository.AdminConfigValueRepository;
import kr.ac.kookmin.stream.internal.domain.config.service.AdminConfigValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class AdminConfigValueServiceImpl implements AdminConfigValueService {

    private final AdminConfigValueRepository adminConfigValueRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getValue(String configKey) {
        return adminConfigValueRepository.findByKey(configKey).map(AdminConfigValue::getConfigValue);
    }

    @Override
    @Transactional
    public AdminConfigValue upsertValue(String configKey, String configValue) {
        adminConfigValueRepository.upsert(configKey, configValue);
        return adminConfigValueRepository.findByKey(configKey)
            .orElseThrow(() -> new IllegalStateException("upsert 직후 값을 찾을 수 없습니다: " + configKey));
    }
}
