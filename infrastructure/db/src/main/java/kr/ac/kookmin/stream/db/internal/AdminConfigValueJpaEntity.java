package kr.ac.kookmin.stream.db.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.internal.domain.config.domain.AdminConfigValue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin_config_values")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminConfigValueJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_key", nullable = false)
    private String configKey;

    @Column(name = "config_value", nullable = false)
    private String configValue;

    private AdminConfigValueJpaEntity(AdminConfigValue configValue) {
        this.id = configValue.getId();
        this.configKey = configValue.getConfigKey();
        this.configValue = configValue.getConfigValue();
    }

    public static AdminConfigValueJpaEntity from(AdminConfigValue configValue) {
        return new AdminConfigValueJpaEntity(configValue);
    }

    public AdminConfigValue toDomain() {
        return AdminConfigValue.of(id, configKey, configValue);
    }
}
