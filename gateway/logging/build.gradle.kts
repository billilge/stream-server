plugins {
    java
}

description = "MDC 기반 요청 추적 — MdcFilter/MdcUserIdFilter/AccessLogFilter"

dependencies {
    implementation(project(":core:common"))

    implementation(platform(libs.springBootDependenciesBom))
    implementation(libs.springWeb)
    implementation(libs.springContext)
    implementation(libs.jakartaServletApi)
    implementation(libs.slf4jApi)

    testImplementation(platform(libs.springBootDependenciesBom))
    testImplementation(libs.springBootStarterTest)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
