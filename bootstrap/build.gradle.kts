plugins {
    java
    alias(libs.plugins.springBoot)
}

description = "실행 가능한 애플리케이션 — 최종 조립, verify() 테스트, 스케줄링 활성화"

dependencies {
    implementation(project(":api:common-api"))
    implementation(project(":api:admin-api"))
    implementation(project(":api:app-api"))
    implementation(project(":gateway:auth"))
    implementation(project(":gateway:logging"))
    implementation(project(":infrastructure:db"))
    implementation(project(":infrastructure:client"))
    implementation(project(":infrastructure:outbox"))
    implementation(project(":core:common"))

    implementation(platform(libs.springBootDependenciesBom))
    implementation(libs.springBootStarter)
    implementation(libs.springBootStarterActuator)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(platform(libs.springBootDependenciesBom))
    testImplementation(platform(libs.springModulithBom))
    testImplementation(libs.springBootStarterTest)
    testImplementation(libs.springModulithStarterTest)
    testImplementation(libs.springModulithDocs)
    testImplementation(libs.archunitJunit5)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    testRuntimeOnly(libs.junitPlatformLauncher)
    // infrastructure:db가 끌어오는 JPA 자동구성이 DataSource를 요구하므로 테스트 전용 인메모리 DB로 컨텍스트 로딩을 지원
    testRuntimeOnly(libs.h2)
}
