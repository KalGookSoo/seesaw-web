package kr.me.seesaw.service;

import kr.me.seesaw.core.authentication.PrincipalProvider;
import kr.me.seesaw.domain.Site;
import kr.me.seesaw.dto.model.UserPrincipal;
import kr.me.seesaw.model.UserModel;
import kr.me.seesaw.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Profile("!prod")
@Transactional
@RequiredArgsConstructor
@Service
public class ConsoleMailService implements MailService {

    private final SiteRepository siteRepository;

    private final PrincipalProvider principalProvider;

    private final MailProperties properties;

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
        String prefix = "[" + site.getName() + "]";
        send(from, to, prefix + title, content);
    }

    @Override
    public void sendToHelpdesk(String title, String content) {
        Authentication authentication = principalProvider.getAuthentication();
        UserPrincipal principal = Optional.ofNullable(authentication.getPrincipal())
                .filter(UserPrincipal.class::isInstance)
                .map(UserPrincipal.class::cast)
                .orElseThrow(() -> new AuthorizationDeniedException("계정 정보를 찾을 수 없습니다."));
        UserModel user = principal.getUser();
        String from = user.getEmail();
        String to = properties.getUsername();
        send(from, to, title, content);
    }

    public void send(String from, String to, String title, String text) {
        log.info("메일을 전송합니다.\n발신자: {}\n수신자: {}\n제목: {}", from, to, title);
    }

    public void send(String from, String to, String title, String viewName, Map<String, String> values) {
        log.info("메일을 전송합니다.\n발신자: {}\n수신자: {}\n제목: {}", from, to, title);
    }

    public void send(String from, String to, String title, String text, String attachment) {
        log.info("메일을 전송합니다.\n발신자: {}\n수신자: {}\n제목: {}", from, to, title);
    }

}
