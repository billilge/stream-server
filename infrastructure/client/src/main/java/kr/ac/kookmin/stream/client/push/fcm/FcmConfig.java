package kr.ac.kookmin.stream.client.push.fcm;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * {@link FcmPushNotificationClient}가 쓰는 Firebase SDK 빈 설정.
 * 서비스 계정 JSON은 파일 마운트 없이 env 하나로 주입하도록 base64 인코딩 값으로 받는다.
 */
@Configuration
@ConditionalOnProperty(prefix = "push", name = "type", havingValue = "fcm")
public class FcmConfig {

    // FirebaseApp에는 close()가 없어 스프링이 종료 메서드를 추론하지 못하므로 delete를 직접 지정한다
    @Bean(destroyMethod = "delete")
    public FirebaseApp firebaseApp(FcmProperties properties) {
        // FcmProperties는 로그 모드에서도 바인딩되므로 필수값 검사는 fcm 모드에서만 뜨는 이곳에서 한다
        if (!StringUtils.hasText(properties.credentialsBase64())) {
            throw new IllegalStateException("push.type=fcm이면 FIREBASE_CREDENTIALS_BASE64가 필요하다");
        }
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(parseCredentials(properties.credentialsBase64()))
            .build();
        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    // MIME 디코더는 base64가 아닌 문자를 건너뛰어 placeholder도 예외 없이 디코딩되고, 실패는 JSON 파싱에서 난다.
    // 어느 단계에서 실패하든 SDK 예외만으로는 어떤 설정이 문제인지 알 수 없으므로 환경변수 오류로 바꿔 던진다
    private ServiceAccountCredentials parseCredentials(String credentialsBase64) {
        try {
            // Linux base64는 76자마다 줄바꿈을 넣으므로 줄바꿈을 무시하는 MIME 디코더를 쓴다
            byte[] credentialsJson = Base64.getMimeDecoder().decode(credentialsBase64);
            return ServiceAccountCredentials.fromStream(new ByteArrayInputStream(credentialsJson));
        } catch (IllegalArgumentException | IOException e) {
            throw new IllegalStateException("FIREBASE_CREDENTIALS_BASE64가 올바른 서비스 계정 JSON의 base64 값이 아니다", e);
        }
    }
}
