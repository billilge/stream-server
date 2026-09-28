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
    public FirebaseApp firebaseApp(FcmProperties properties) throws IOException {
        // FcmProperties는 로그 모드에서도 바인딩되므로 필수값 검사는 fcm 모드에서만 뜨는 이곳에서 한다
        if (!StringUtils.hasText(properties.credentialsBase64())) {
            throw new IllegalStateException("push.type=fcm이면 FIREBASE_CREDENTIALS_BASE64가 필요하다");
        }
        // Linux base64는 76자마다 줄바꿈을 넣으므로 줄바꿈을 무시하는 MIME 디코더를 쓴다
        byte[] credentialsJson = Base64.getMimeDecoder().decode(properties.credentialsBase64());
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(ServiceAccountCredentials.fromStream(new ByteArrayInputStream(credentialsJson)))
            .build();
        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
