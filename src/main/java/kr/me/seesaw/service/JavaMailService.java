package kr.me.seesaw.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.me.seesaw.core.authentication.PrincipalProvider;
import kr.me.seesaw.core.file.FileManager;
import kr.me.seesaw.domain.Site;
import kr.me.seesaw.dto.model.UserPrincipal;
import kr.me.seesaw.message.CmsMessageSource;
import kr.me.seesaw.model.UserModel;
import kr.me.seesaw.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Profile("prod")
@Transactional
@RequiredArgsConstructor
@Service
public class JavaMailService implements MailService {

    private final SiteRepository siteRepository;

    private final JavaMailSender sender;

    private final MailProperties properties;

    private final FileManager fileManager;

    private final SpringTemplateEngine springTemplateEngine;

    private final PrincipalProvider principalProvider;

    private final CmsMessageSource messageSource;

    @Override
    public void sendToReport(String siteId, String title, String content) {
        Authentication authentication = principalProvider.getAuthentication();
        UserPrincipal principal = Optional.ofNullable(authentication.getPrincipal())
                .filter(UserPrincipal.class::isInstance)
                .map(UserPrincipal.class::cast)
                .orElseThrow(() -> new AuthorizationDeniedException("계정 정보를 찾을 수 없습니다."));
        UserModel user = principal.getUser();
        String from = user.getEmail();
        String to = properties.getUsername();

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new NoSuchElementException("사이트를 찾을 수 없습니다. siteId: " + siteId));

        String tag = messageSource.getMessage("label.report");
        String prefix = String.format("[%s][%s] ", tag, site.getName());
        send(from, to, prefix + title, content);
    }

    @Override
    public void sendToHelpdesk(String siteId, String title, String content) {
        Authentication authentication = principalProvider.getAuthentication();
        UserPrincipal principal = Optional.ofNullable(authentication.getPrincipal())
                .filter(UserPrincipal.class::isInstance)
                .map(UserPrincipal.class::cast)
                .orElseThrow(() -> new AuthorizationDeniedException("계정 정보를 찾을 수 없습니다."));
        UserModel user = principal.getUser();
        String from = user.getEmail();
        String to = properties.getUsername();

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new NoSuchElementException("사이트를 찾을 수 없습니다. siteId: " + siteId));

        String tag = messageSource.getMessage("label.inquiry");
        String prefix = String.format("[%s][%s] ", tag, site.getName());
        send(from, to, prefix + title, content);
    }

    private void send(String from, String to, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(title);
        message.setText(text);
        log.info("메일을 전송합니다.\n발신자: {}\n수신자: {}\n제목: {}", from, to, message.getSubject());
        sender.send(message);
    }

    private void send(String from, String to, String title, String viewName, Map<String, String> values) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "utf-8");

        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(title);

        // 템플릿에 전달할 데이터 설정
        Context context = new Context();
        values.forEach(context::setVariable);
        String html = springTemplateEngine.process(viewName, context);
        mimeMessageHelper.setText(html, true);

        log.info("메일을 전송합니다.\n발신자: {}\n수신자: {}\n제목: {}", from, to, title);
        sender.send(message);
    }

    private void send(String from, String to, String title, String text, String attachment) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(title);
        mimeMessageHelper.setText(text);

        FileSystemResource fileSystemResource = new FileSystemResource(new File(StringUtils.cleanPath(attachment)));
//        ByteArrayInputStream stream = fileManager.read(attachment);

        mimeMessageHelper.addAttachment(fileSystemResource.getFilename(), fileSystemResource);

        log.info("메일을 전송합니다.\n발신자: {}\n수신자: {}\n제목: {}", from, to, title);
        sender.send(message);
    }

}
