package kr.ac.kookmin.stream.bootstrap.config;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import kr.ac.kookmin.stream.common.DateUtil;
import org.springframework.context.annotation.Configuration;

/**
 * JVM 기본 시간대를 서울로 고정한다. 컨테이너 시간대(UTC)와 관계없이 {@code LocalDateTime.now()} 등이 한국 시간을 쓰게 한다.
 */
@Configuration
public class TimezoneConfig {

    @PostConstruct
    void setDefaultTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone(DateUtil.KST));
    }
}
