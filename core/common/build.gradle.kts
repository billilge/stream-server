plugins {
    java
}

description = "공유 커널 — 순수 Java. Spring/Modulith/web/security/JPA 의존 없음. verify 설정에서 shared module로 선언"

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(platform(libs.junitBom))
    testImplementation(libs.junitJupiter)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
