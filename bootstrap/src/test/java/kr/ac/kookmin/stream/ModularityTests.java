package kr.ac.kookmin.stream;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTests {

    private final ApplicationModules modules = ApplicationModules.of(StreamServerApplication.class);

    @Test
    void verify() {
        modules.verify();
    }

    @Test
    void writeDocs() {
        new Documenter(modules).writeDocumentation();
    }

}
