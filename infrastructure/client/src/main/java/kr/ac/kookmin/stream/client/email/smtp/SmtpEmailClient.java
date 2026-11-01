package kr.ac.kookmin.stream.client.email.smtp;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Locale;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.RetryExecutor;
import kr.ac.kookmin.stream.common.TaskExecutorNames;
import kr.ac.kookmin.stream.member.domain.verification.client.EmailClient;
import kr.ac.kookmin.stream.member.domain.verification.domain.EmailMessage;
import kr.ac.kookmin.stream.member.domain.verification.domain.EmailVerificationErrorCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@ConditionalOnProperty(prefix = "email", name = "type", havingValue = "smtp")
@RequiredArgsConstructor
public class SmtpEmailClient implements EmailClient {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailClient.class);

    private static final int MAX_RETRIES = 2;
    private static final Duration RETRY_DELAY = Duration.ofSeconds(2);

    private final SmtpEmailProperties properties;
    private final JavaMailSender javaMailSender;
    private final RetryExecutor retryExecutor;

    @Qualifier("emailTemplateEngine")
    private final TemplateEngine emailTemplateEngine;

    @Override
    public void send(EmailMessage message) {
        validateFromConfigured();
        MimeMessage mimeMessage = toMimeMessage(message);
        try {
            retryExecutor.executeWithRetry(
                () -> javaMailSender.send(mimeMessage),
                SmtpEmailClient::isTransient,
                MAX_RETRIES,
                RETRY_DELAY
            );
        } catch (MailAuthenticationException e) {
            log.error("SMTP 인증 실패(설정 확인 필요)", e);
            throw new BusinessException(EmailVerificationErrorCode.EMAIL_SEND_FAILED);
        } catch (MailException e) {
            // 거절된 받는 주소가 예외 메시지에 들어 있을 수 있어 원인 예외의 종류만 남긴다
            log.warn("메일 발송 실패: {}", e.getMostSpecificCause().getClass().getName());
            throw new BusinessException(EmailVerificationErrorCode.EMAIL_SEND_FAILED);
        }
    }

    @Override
    @Async(TaskExecutorNames.EMAIL_TASK_EXECUTOR)
    public void sendAsync(EmailMessage message) {
        try {
            send(message);
        } catch (BusinessException e) {
            // send가 실패를 로그로 남긴다
        }
    }

    // 연결 실패·시간 초과 같은 네트워크 오류만 다시 보낸다
    private static boolean isTransient(Throwable e) {
        if (!(e instanceof MailSendException sendException)) {
            return false;
        }
        return hasIoCause(sendException)
            || Arrays.stream(sendException.getMessageExceptions()).anyMatch(SmtpEmailClient::hasIoCause);
    }

    private static boolean hasIoCause(Throwable e) {
        for (Throwable cause = e; cause != null; cause = cause.getCause()) {
            if (cause instanceof IOException) {
                return true;
            }
        }
        return false;
    }

    private void validateFromConfigured() {
        if (!StringUtils.hasText(properties.from())) {
            throw new IllegalStateException("email.type=smtp이면 보내는 주소(EMAIL_FROM 또는 SMTP_USERNAME)가 필요하다");
        }
    }

    private MimeMessage toMimeMessage(EmailMessage message) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
            if (StringUtils.hasText(properties.fromName())) {
                helper.setFrom(properties.from(), properties.fromName());
            } else {
                helper.setFrom(properties.from());
            }
            helper.setTo(message.to());
            helper.setSubject(message.subject());
            helper.setText(renderBody(message), true);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new IllegalStateException("메일 메시지를 만들지 못했습니다: " + e.getClass().getName(), e);
        }
        return mimeMessage;
    }

    private String renderBody(EmailMessage message) {
        Context context = new Context(Locale.KOREAN, message.variables());
        return emailTemplateEngine.process(message.template().templateName(), context);
    }
}
