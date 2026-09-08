package kr.ac.kookmin.stream.internal.domain.config.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminConfigValue {

    private Long id;
    private String configKey;
    private String configValue;

    public static AdminConfigValue of(Long id, String configKey, String configValue) {
        return new AdminConfigValue(id, configKey, configValue);
    }
}
