plugins {
    java
}

description = "공유 커널 — 순수 Java + spring-context·spring-tx만 허용. web/security/JPA 의존 없음(modulith-api는 루트 공통 compileOnly로만 상속). verify 설정에서 shared module로 선언"

dependencies {
    // LockExecutor가 시도마다 트랜잭션을 열고(spring-tx) 빈으로 등록된다(spring-context)
    implementation(platform(libs.springBootDependenciesBom))
    implementation(libs.springContext)
    implementation(libs.springTx)

    testImplementation(platform(libs.junitBom))
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
