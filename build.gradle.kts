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

    // 메서드 파라미터 이름을 바이트코드에 보존한다. 미적용 시 @RequestParam 등이
    // Swagger에서 arg0/arg1로 노출된다. (Spring Boot 플러그인 미적용 모듈까지 일괄 적용)
    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
    }

    // 모듈별 build.gradle.kts에서 반복 선언하지 않도록 lombok을 공통 의존성으로 적용
    // (subprojects {} 클로저 내부 리시버 기준으로는 libs 카탈로그 액세서가 아직 등록되지 않아 rootProject를 통해 참조)
    dependencies {
        "compileOnly"(rootProject.libs.lombok)
        "annotationProcessor"(rootProject.libs.lombok)
        "testCompileOnly"(rootProject.libs.lombok)
        "testAnnotationProcessor"(rootProject.libs.lombok)

        // 모듈 공개 처리(@ApplicationModule·@NamedInterface 등)에 거의 모든 모듈이 사용하므로 공통 적용.
        // compileOnly 어노테이션 전용 — 경계 검증은 bootstrap 테스트가 수행한다.
        "compileOnly"(platform(rootProject.libs.springModulithBom))
        "compileOnly"(rootProject.libs.springModulithApi)
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
