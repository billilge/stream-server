plugins {
    java
}

description = "AI 서버(stream-ai) — 내부 토큰, /v1/ai/**, 조회 전용"

// 학생에게 보이는 글만 내보내도록 학생 화면이 쓰는 조회 서비스를 그대로 쓴다. 쓰기용 서비스는 부르지 않는다
dependencies {
    implementation(project(":api:common-api"))
    implementation(project(":core:common"))
    implementation(project(":core:domain:event"))
    implementation(project(":core:domain:welfare"))

    implementation(platform(libs.springBootDependenciesBom))
    implementation(libs.springBootStarterWebmvc)
    implementation(libs.springdocStarterWebmvcUi)
}
