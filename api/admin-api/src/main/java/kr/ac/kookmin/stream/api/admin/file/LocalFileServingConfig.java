package kr.ac.kookmin.stream.api.admin.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 로컬 스토리지에 저장된 파일을 공개 GET으로 읽을 수 있게 하는 임시 설정.
 * S3 연동 전 임시 서빙이며, S3로 전환하면 {@link AdminLocalFileUploadController}와 함께 제거한다.
 */
@Configuration
@ConditionalOnProperty(prefix = "file.storage", name = "type", havingValue = "local", matchIfMissing = true)
public class LocalFileServingConfig implements WebMvcConfigurer {

    @Value("${file.storage.local.base-path}")
    private String basePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
            .addResourceLocations("file:" + basePath + "/files/");
    }
}
