package kr.ac.kookmin.stream.api.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCodeCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 기본 메타(info·JWT 인증)와 운영진/학생 앱 그룹 분리를 정의한다.
 * 에러 응답 문서화는 {@link ApiErrorCodeCustomizer}가 담당한다.
 */
@Configuration
public class OpenApiConfig {

    private static final String JWT_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("소프트웨어융합대학 학생회 플랫폼 API")
                .version("v1"))
            .components(new Components().addSecuritySchemes(JWT_SCHEME, new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")))
            .addSecurityItem(new SecurityRequirement().addList(JWT_SCHEME));
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
            .group("admin")
            .pathsToMatch("/v1/admin/**")
            .build();
    }

    @Bean
    public GroupedOpenApi appApi() {
        return GroupedOpenApi.builder()
            .group("app")
            .pathsToMatch("/v1/app/**")
            .build();
    }
}
