plugins {
    java
}

description = "운영진 콘솔 — ADMIN, /v1/admin/**"

dependencies {
    implementation(project(":api:common-api"))
    implementation(project(":core:common"))
    implementation(project(":core:domain:internal"))
    implementation(project(":core:domain:member"))
    implementation(project(":core:domain:welfare"))
    implementation(project(":gateway:auth"))
    implementation(project(":gateway:logging"))

    implementation(platform(libs.springBootDependenciesBom))
    implementation(libs.springBootStarterWebmvc)
    implementation(libs.springBootStarterValidation)
    implementation(libs.springBootStarterAspectj)
    implementation(libs.springTx)
    implementation(libs.springdocStarterWebmvcUi)

    testImplementation(platform(libs.springBootDependenciesBom))
    testImplementation(libs.springBootStarterTest)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
