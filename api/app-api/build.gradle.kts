plugins {
    java
}

description = "학생 앱 — STUDENT, /v1/app/**"

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
