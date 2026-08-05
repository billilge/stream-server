plugins {
    java
}

description = "외부 API 클라이언트 구현체"

dependencies {
    implementation(project(":core:common"))

    implementation(platform(libs.springBootDependenciesBom))
    implementation(libs.springWeb)
    implementation(libs.jacksonDatabind)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(platform(libs.springBootDependenciesBom))
    testImplementation(libs.springBootStarterTest)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
