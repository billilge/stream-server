plugins {
    java
}

description = "JPA Entity, RepositoryImpl, Flyway 마이그레이션 (MySQL)"

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain:member"))

    implementation(platform(libs.springBootDependenciesBom))
    implementation(libs.springBootStarterDataJpa)
    implementation(libs.springBootStarterFlyway)
    implementation(libs.flywayMysql)
    runtimeOnly(libs.mysqlConnectorJ)

    testImplementation(platform(libs.springBootDependenciesBom))
    testImplementation(libs.springBootStarterDataJpaTest)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
