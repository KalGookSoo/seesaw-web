package kr.me.seesaw.service;

import kr.me.seesaw.core.authentication.PrincipalProvider;
import kr.me.seesaw.domain.Site;
import kr.me.seesaw.dto.model.UserPrincipal;
import kr.me.seesaw.message.CmsMessageSource;
import kr.me.seesaw.model.UserModel;
import kr.me.seesaw.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class DefaultReportService implements ReportService {

    private final SiteRepository siteRepository;

    private final PrincipalProvider principalProvider;

    private final MailProperties properties;

    private final CmsMessageSource messageSource;

    private final MailService mailService;

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

        Map<String, String> values = Map.of(
                "from", from,
                "siteName", site.getName(),
                "title", title,
                "content", content
        );
        mailService.send(from, new String[]{to}, prefix + title, "mail/send_to_report", values);
        logPreviewUrl(from, "/mail/preview/send-to-report");
    }

    public void logPreviewUrl(String from, String path) {
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentContextPath();
        String previewUrl = builder.path(path)
                .queryParam("from", from)
                .build()
                .toUriString();
        log.info("미리보기 URL: {}", previewUrl);
    }

}
