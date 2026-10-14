package kr.ac.kookmin.stream.bootstrap.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * {@code @Scheduled} 작업(아웃박스 릴레이 등)을 켠다.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
