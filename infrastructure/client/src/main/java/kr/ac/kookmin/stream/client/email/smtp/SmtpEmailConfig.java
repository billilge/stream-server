package kr.ac.kookmin.stream.client.email.smtp;

import java.nio.charset.StandardCharsets;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * 메일 본문용 Thymeleaf 엔진. 웹 뷰 리졸버까지 등록하는 thymeleaf starter 대신 엔진만 둔다.
 */
@Configuration
@ConditionalOnProperty(prefix = "email", name = "type", havingValue = "smtp")
public class SmtpEmailConfig {

    private static final String TEMPLATE_PREFIX = "templates/email/";
    private static final String TEMPLATE_SUFFIX = ".html";

    @Bean
    public TemplateEngine emailTemplateEngine() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix(TEMPLATE_PREFIX);
        templateResolver.setSuffix(TEMPLATE_SUFFIX);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setCheckExistence(true);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }
}
