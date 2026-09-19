package kr.ac.kookmin.stream.internal.domain.config.service;

import java.util.Map;
import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.config.domain.AdminConfigValue;

public interface AdminConfigValueService {
    Optional<String> getValue(String configKey);
    AdminConfigValue upsertValue(String configKey, String configValue);

    // 여러 key-value를 한 트랜잭션에서 upsert한다. 일부만 반영되는 상태를 막기 위해 개별 upsertValue를
    // 여러 번 호출하는 대신 이 메서드로 묶는다.
    void upsertValues(Map<String, String> values);
}
