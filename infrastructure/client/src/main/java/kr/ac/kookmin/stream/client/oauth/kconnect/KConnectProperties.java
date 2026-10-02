package kr.ac.kookmin.stream.client.oauth.kconnect;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * KConnect 연동 설정. KConnect 측 요청으로 API 명세(주소·경로·응답 필드)를 코드에 두지 않고 환경 변수로만 받는다.
 * <p>
 * allowedRedirectUris는 KConnect에 등록한 redirect URI와 글자 하나까지 같아야 한다. 운영 환경에는 localhost를 넣지 않는다.
 */
@ConfigurationProperties(prefix = "oauth.kconnect")
public record KConnectProperties(
    String baseUrl,
    String tokenPath,
    String clientId,
    String clientSecret,
    Set<String> allowedRedirectUris,
    UserInfo userInfo,
    Duration connectTimeout,
    Duration readTimeout
) {

    /** 사용자 정보 API의 경로와, 응답에서 읽을 필드 이름. */
    public record UserInfo(
        String path,
        String idField,
        String studentIdField,
        String nameField,
        String majorField,
        String academicStatusField
    ) {}

    /** 비어 있는 필수 설정의 키 이름. 로그에 남기므로 값은 담지 않는다. */
    public List<String> missingSettings() {
        UserInfo info = userInfo != null ? userInfo : new UserInfo(null, null, null, null, null, null);

        Map<String, String> required = new LinkedHashMap<>();
        required.put("base-url", baseUrl);
        required.put("token-path", tokenPath);
        required.put("client-id", clientId);
        required.put("client-secret", clientSecret);
        required.put("user-info.path", info.path());
        required.put("user-info.id-field", info.idField());
        required.put("user-info.student-id-field", info.studentIdField());
        required.put("user-info.name-field", info.nameField());
        required.put("user-info.major-field", info.majorField());
        required.put("user-info.academic-status-field", info.academicStatusField());

        return required.entrySet().stream()
            .filter(entry -> entry.getValue() == null || entry.getValue().isBlank())
            .map(Map.Entry::getKey)
            .toList();
    }
}
