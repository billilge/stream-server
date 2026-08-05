plugins {
    java
}

description = "MDC 기반 요청 추적 — MdcFilter/LoggingFilter, access log"

dependencies {
    implementation(project(":core:common"))

    implementation(platform(libs.springBootDependenciesBom))
    implementation(libs.springWeb)
    implementation(libs.springContext)
    implementation(libs.jakartaServletApi)

    testImplementation(platform(libs.springBootDependenciesBom))
    testImplementation(libs.springBootStarterTest)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
