plugins {
    java
}

description = "외부 API 클라이언트 구현체"

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain:internal"))

    implementation(platform(libs.springBootDependenciesBom))
    implementation(libs.springBootStarter)
    implementation(libs.springWeb)
    implementation(libs.jacksonDatabind)

    implementation(platform(libs.awsSdkBom))
    implementation(libs.awsS3)

    testImplementation(platform(libs.springBootDependenciesBom))
    testImplementation(libs.springBootStarterTest)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
