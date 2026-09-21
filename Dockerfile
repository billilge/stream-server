# syntax=docker/dockerfile:1

# ---- Build stage: Gradle wrapper로 실행 가능한 layered jar 생성 ----
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

# 소스 전체 복사 (build 산출물·.git·.idea 등은 .dockerignore로 제외)
COPY . .

# Gradle 캐시를 마운트해 재빌드를 빠르게 한다. bootJar는 테스트를 실행하지 않는다.
# application.jar로 이름을 고정해 extract 이후 ENTRYPOINT가 태그·버전에 흔들리지 않게 한다.
RUN --mount=type=cache,target=/root/.gradle \
    chmod +x gradlew && \
    ./gradlew :bootstrap:bootJar --no-daemon && \
    cp bootstrap/build/libs/bootstrap-*.jar application.jar && \
    java -Djarmode=tools -jar application.jar extract --layers --destination extracted

# ---- Runtime stage: JRE에 레이어별로 복사 ----
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

# 비루트 사용자로 실행
RUN groupadd --system spring && useradd --system --gid spring spring

# 변경 빈도가 낮은 레이어부터 복사해 이미지 레이어 캐시 적중률을 높인다
COPY --from=build --chown=spring:spring /workspace/extracted/dependencies/ ./
COPY --from=build --chown=spring:spring /workspace/extracted/spring-boot-loader/ ./
COPY --from=build --chown=spring:spring /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=spring:spring /workspace/extracted/application/ ./

USER spring
EXPOSE 8080

# 프로파일은 이미지에 박지 않고 런타임 env(SPRING_PROFILES_ACTIVE)로 주입한다.
# JAVA_OPTS로 힙·GC 등을 넘길 수 있게 하고, exec로 java를 PID 1로 만들어 SIGTERM(graceful shutdown)을 받게 한다.
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar application.jar"]
