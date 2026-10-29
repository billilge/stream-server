package kr.ac.kookmin.stream.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

// 메인 클래스는 bootstrap 패키지에 두지만, 빈·JPA 엔티티와 레포지토리·설정 바인딩은 모든 모듈이 있는 루트 패키지에서 찾는다
@SpringBootApplication(scanBasePackages = StreamServerApplication.BASE_PACKAGE)
@AutoConfigurationPackage(basePackages = StreamServerApplication.BASE_PACKAGE)
@ConfigurationPropertiesScan(StreamServerApplication.BASE_PACKAGE)
public class StreamServerApplication {

    static final String BASE_PACKAGE = "kr.ac.kookmin.stream";

    public static void main(String[] args) {
        SpringApplication.run(StreamServerApplication.class, args);
    }

}
