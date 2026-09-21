package kr.ac.kookmin.stream.api.common.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import java.util.List;
import kr.ac.kookmin.stream.api.common.config.OpenApiConfig;
import kr.ac.kookmin.stream.security.PublicEndpoints;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.stereotype.Component;

/**
 * 공개 엔드포인트({@link PublicEndpoints})의 보안 요구사항을 비워, {@link OpenApiConfig}가 전역으로 건
 * JWT 요구사항이 permitAll API에 "인증 필요"로 잘못 표시되지 않게 한다.
 *
 * <p>핸들러 메서드만 알고 경로는 모르는 {@code OperationCustomizer}와 달리, 경로를 아는
 * {@code OpenApiCustomizer}를 써서 {@link PublicEndpoints#isPublic(String)}로 판정한다.
 * springdoc이 빈으로 자동 감지해 모든 그룹에 적용한다.
 */
@Component
public class PublicPathSecurityCustomizer implements OpenApiCustomizer {

    @Override
    public void customise(OpenAPI openApi) {
        openApi.getPaths().forEach((path, item) -> {
            if (PublicEndpoints.isPublic(path)) {
                item.readOperations().forEach(operation -> operation.setSecurity(List.of()));
            }
        });
    }
}
