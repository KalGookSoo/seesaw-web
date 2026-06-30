package kr.me.seesaw.web.domain.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Profile("prod")
@Transactional
@RequiredArgsConstructor
@Service
public class JavaMailService implements MailService {

    private final JavaMailSender sender;

    private final MailProperties properties;

    private final SpringTemplateEngine springTemplateEngine;

    @Override
    public void send(@NonNull String from, @NonNull String[] to, @NonNull String title, @NonNull String viewName, @NonNull Map<String, String> values) {
        log.info("메일을 전송합니다.\n발신자: {}\n수신자: {}\n제목: {}", from, to, title);
        MimeMessage message = sender.createMimeMessage();
        Context context = new Context();
        values.forEach(context::setVariable);
        String html = springTemplateEngine.process(viewName, context);

        MimeMessageHelper mimeMessageHelper;
        try {
            mimeMessageHelper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            mimeMessageHelper.setFrom(properties.getUsername());
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setReplyTo(from);
            mimeMessageHelper.setSubject(title);
            mimeMessageHelper.setText(html, true);
        } catch (MessagingException e) {
            log.error("메시지 생성에 실패했습니다.");
            return;
        }
        sender.send(message);
    }

}
