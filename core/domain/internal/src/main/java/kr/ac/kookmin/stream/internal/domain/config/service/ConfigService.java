package kr.ac.kookmin.stream.internal.domain.config.service;

import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.config.domain.AdminConfigValue;

public interface ConfigService {
    Optional<String> getValue(String configKey);
    AdminConfigValue upsertValue(String configKey, String configValue);
}
