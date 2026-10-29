package kr.ac.kookmin.stream.bootstrap;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTests {

    // 메인 클래스 패키지(bootstrap)가 아니라 루트 패키지를 기준으로 모듈을 나눈다
    private final ApplicationModules modules = ApplicationModules.of(StreamServerApplication.BASE_PACKAGE);

    @Test
    void verify() {
        modules.verify();
    }

    @Test
    void writeDocs() {
        new Documenter(modules).writeDocumentation();
    }

}
