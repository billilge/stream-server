plugins {
    java
}

description = "공유 커널 — 순수 Java. web/security/JPA·Spring 런타임 의존 없음(modulith-api는 루트 공통 compileOnly로만 상속). verify 설정에서 shared module로 선언"

dependencies {
    testImplementation(platform(libs.junitBom))
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
