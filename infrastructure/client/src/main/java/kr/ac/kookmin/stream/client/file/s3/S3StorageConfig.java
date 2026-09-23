package kr.ac.kookmin.stream.client.file.s3;

import java.net.URI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * {@link S3FileStorageClient}가 쓰는 S3 SDK 클라이언트 빈 설정. 빈으로 등록해 두면 종료 시 스프링이 {@code close()}를 호출한다.
 * R2는 AWS S3 API와 호환되므로 {@code endpointOverride}로 R2 엔드포인트를 지정하고 region은 {@code auto}를 쓴다.
 * R2에는 EC2 인스턴스 프로필 같은 자동 자격증명 체인이 없어, R2 API 토큰의 액세스 키·시크릿 키를 정적으로 주입한다.
 */
@Configuration
@ConditionalOnProperty(prefix = "file.storage", name = "type", havingValue = "s3")
public class S3StorageConfig {

    @Bean
    public S3Client s3Client(S3FileStorageProperties properties) {
        return S3Client.builder()
            .region(Region.of(properties.region()))
            .endpointOverride(URI.create(properties.endpoint()))
            .credentialsProvider(credentialsProvider(properties))
            .build();
    }

    @Bean
    public S3Presigner s3Presigner(S3FileStorageProperties properties) {
        return S3Presigner.builder()
            .region(Region.of(properties.region()))
            .endpointOverride(URI.create(properties.endpoint()))
            .credentialsProvider(credentialsProvider(properties))
            .build();
    }

    private StaticCredentialsProvider credentialsProvider(S3FileStorageProperties properties) {
        return StaticCredentialsProvider.create(
            AwsBasicCredentials.create(properties.accessKey(), properties.secretKey()));
    }
}
