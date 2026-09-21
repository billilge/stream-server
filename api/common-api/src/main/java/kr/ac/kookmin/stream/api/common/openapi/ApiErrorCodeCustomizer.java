package kr.ac.kookmin.stream.api.common.openapi;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.common.ErrorCode;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

/**
 * 핸들러 메서드의 {@link ApiErrorCode}를 읽어 OpenAPI 응답에 에러 예시를 추가하는 {@code OperationCustomizer}.
 * springdoc이 빈으로 자동 감지해 모든 그룹에 적용한다.
 *
 * <p>같은 HTTP status의 코드가 여러 개면 한 응답에 example을 여러 개 붙인다(그룹핑하지 않으면 덮인다).
 * {@code codes}에 enum에 없는 이름이 있으면 즉시 예외로 실패해 오탈자를 잡는다.
 */
@Component
public class ApiErrorCodeCustomizer implements OperationCustomizer {

    private static final String APPLICATION_JSON = "application/json";

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        Set<ApiErrorCode> annotations = AnnotatedElementUtils.findMergedRepeatableAnnotations(
            handlerMethod.getMethod(), ApiErrorCode.class, ApiErrorCodes.class);
        if (annotations.isEmpty()) {
            return operation;
        }

        Map<Integer, List<ErrorCode>> codesByStatus = new LinkedHashMap<>();
        for (ApiErrorCode annotation : annotations) {
            for (ErrorCode errorCode : resolve(annotation)) {
                codesByStatus.computeIfAbsent(errorCode.status(), status -> new ArrayList<>())
                    .add(errorCode);
            }
        }

        ApiResponses responses = operation.getResponses();
        codesByStatus.forEach((status, errorCodes) -> addExamples(responses, status, errorCodes));
        return operation;
    }

    private List<ErrorCode> resolve(ApiErrorCode annotation) {
        Class<? extends ErrorCode> type = annotation.type();
        ErrorCode[] constants = type.getEnumConstants();
        if (constants == null) {
            throw new IllegalStateException(
                "@ApiErrorCode type은 enum이어야 합니다: " + type.getName());
        }
        Map<String, ErrorCode> byName = new LinkedHashMap<>();
        for (ErrorCode constant : constants) {
            byName.put(constant.name(), constant);
        }

        List<ErrorCode> resolved = new ArrayList<>();
        for (String code : annotation.codes()) {
            ErrorCode errorCode = byName.get(code);
            if (errorCode == null) {
                throw new IllegalStateException(
                    "@ApiErrorCode: %s에 존재하지 않는 코드 '%s'".formatted(type.getSimpleName(), code));
            }
            resolved.add(errorCode);
        }
        return resolved;
    }

    private void addExamples(ApiResponses responses, int status, List<ErrorCode> errorCodes) {
        String statusKey = String.valueOf(status);

        // io.swagger...ApiResponse가 dto ApiResponse와 충돌하므로 정규명(FQCN)으로 구분한다.
        io.swagger.v3.oas.models.responses.ApiResponse response = responses.get(statusKey);
        if (response == null) {
            response = new io.swagger.v3.oas.models.responses.ApiResponse().description("오류 응답");
            responses.addApiResponse(statusKey, response);
        }

        Content content = response.getContent();
        if (content == null) {
            content = new Content();
            response.setContent(content);
        }
        MediaType mediaType = content.get(APPLICATION_JSON);
        if (mediaType == null) {
            mediaType = new MediaType();
            content.addMediaType(APPLICATION_JSON, mediaType);
        }

        // 기존 응답이 있으면 example만 병합한다(Map에 put하므로 기존 것을 덮지 않는다).
        for (ErrorCode errorCode : errorCodes) {
            Example example = new Example()
                .summary(errorCode.name())
                .value(ApiResponse.error(errorCode));
            mediaType.addExamples(errorCode.name(), example);
        }
    }
}
