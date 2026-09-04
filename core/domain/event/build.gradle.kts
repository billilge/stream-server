plugins {
    java
}

description = "event 도메인"

dependencies {
    implementation(project(":core:common"))

    implementation(platform(libs.springBootDependenciesBom))
    implementation(libs.springContext)
    implementation(libs.springTx)

    // @ApplicationModule(type = OPEN) 선언용(package-info). 경계 검증은 bootstrap 테스트가 수행한다
    compileOnly(platform(libs.springModulithBom))
    compileOnly(libs.springModulithApi)

    testImplementation(platform(libs.junitBom))
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
