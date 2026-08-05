plugins {
    java
}

description = "운영진 콘솔 — ADMIN, /v1/admin/**"

dependencies {
    implementation(project(":api:common-api"))
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
