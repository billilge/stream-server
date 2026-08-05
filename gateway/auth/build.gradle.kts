plugins {
    java
}

description = "인증/인가 — Spring Security, JWT, DepartmentAccessChecker"

dependencies {
    implementation(project(":core:common"))

    implementation(platform(libs.springBootDependenciesBom))
    implementation(libs.springBootStarterSecurity)
    implementation(libs.springBootStarterWebmvc)
    implementation(libs.jjwtApi)
    runtimeOnly(libs.jjwtImpl)
    runtimeOnly(libs.jjwtJackson)

    testImplementation(platform(libs.springBootDependenciesBom))
    testImplementation(libs.springBootStarterTest)
    testImplementation(libs.springBootStarterSecurityTest)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
