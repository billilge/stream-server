plugins {
    java
}

description = "role 무관 공통 인프라 — ApiResponse, GlobalExceptionHandler, WebMvcConfig, ApiUser, @ApiErrorCode"

dependencies {
    implementation(project(":core:common"))
    implementation(project(":gateway:auth"))
    implementation(project(":gateway:logging"))

    implementation(platform(libs.springBootDependenciesBom))
    implementation(libs.springBootStarterWebmvc)
    implementation(libs.springBootStarterValidation)

    testImplementation(platform(libs.springBootDependenciesBom))
    testImplementation(libs.springBootStarterTest)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
