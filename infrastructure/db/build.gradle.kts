plugins {
    java
}

description = "JPA Entity, RepositoryImpl, Flyway 마이그레이션 (MySQL)"

dependencies {
    implementation(project(":core:common"))

    implementation(platform(libs.springBootDependenciesBom))
    implementation(libs.springBootStarterDataJpa)
    implementation(libs.springBootStarterFlyway)
    implementation(libs.flywayMysql)
    runtimeOnly(libs.mysqlConnectorJ)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(platform(libs.springBootDependenciesBom))
    testImplementation(libs.springBootStarterDataJpaTest)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
