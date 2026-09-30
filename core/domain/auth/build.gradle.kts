plugins {
    java
}

description = "auth 도메인 — gateway:auth(Security/JWT)와는 별개의 도메인 계층 로직"

// gateway:auth와 프로젝트 이름(auth)이 같아 기본 좌표(kr.ac.kookmin:auth)가 겹치면 Gradle이 두 모듈을 같은 모듈로 보고
// 한쪽을 다른 쪽으로 치환한다. group을 달리해 좌표를 분리하고, bootJar 안에서 jar 파일명도 겹치지 않게 바꾼다
group = "kr.ac.kookmin.domain"

base {
    archivesName.set("domain-auth")
}

dependencies {
    implementation(project(":core:common"))

    implementation(platform(libs.springBootDependenciesBom))
    implementation(libs.springContext)
    implementation(libs.springTx)

    testImplementation(platform(libs.junitBom))
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
