plugins {
    alias(libs.plugins.springBoot) apply false
}

allprojects {
    group = "kr.ac.kookmin"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    // Java 21 (LTS) baseline — docs/conventions/architecture.md §1
    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
