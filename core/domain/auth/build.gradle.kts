plugins {
    java
}

description = "auth 도메인 — gateway:auth(Security/JWT)와는 별개의 도메인 계층 로직"

dependencies {
    implementation(project(":core:common"))

    implementation(platform(libs.springBootDependenciesBom))
    implementation(libs.springContext)
    implementation(libs.springTx)

    testImplementation(platform(libs.junitBom))
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
