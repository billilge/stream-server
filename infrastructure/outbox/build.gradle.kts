plugins {
    java
}

description = "아웃박스 릴레이 — 테이블·폴러·재발행. 특정 도메인을 모름"

dependencies {
    implementation(project(":core:common"))

    implementation(platform(libs.springBootDependenciesBom))
    implementation(libs.springContext)
    implementation(libs.springBootStarterDataJpa)

    testImplementation(platform(libs.springBootDependenciesBom))
    testImplementation(libs.springBootStarterDataJpaTest)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
