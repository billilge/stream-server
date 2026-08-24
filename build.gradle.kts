import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    alias(libs.plugins.springBoot) apply false
    alias(libs.plugins.spotless) apply false
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
    apply(plugin = "com.diffplug.spotless")

    // Java 21 (LTS) baseline — docs/conventions/architecture.md §1
    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    // 모듈별 build.gradle.kts에서 반복 선언하지 않도록 lombok을 공통 의존성으로 적용
    // (subprojects {} 클로저 내부 리시버 기준으로는 libs 카탈로그 액세서가 아직 등록되지 않아 rootProject를 통해 참조)
    dependencies {
        "compileOnly"(rootProject.libs.lombok)
        "annotationProcessor"(rootProject.libs.lombok)
        "testCompileOnly"(rootProject.libs.lombok)
        "testAnnotationProcessor"(rootProject.libs.lombok)
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    extensions.configure<SpotlessExtension> {
        java {
            // AOSP 프로파일 — 들여쓰기 4칸, 한 줄 100자
            googleJavaFormat().aosp()
        }
    }
}
