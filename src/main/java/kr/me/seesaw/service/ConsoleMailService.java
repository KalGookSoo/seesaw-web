package kr.me.seesaw.service;

import kr.me.seesaw.core.authentication.PrincipalProvider;
import kr.me.seesaw.domain.Site;
import kr.me.seesaw.dto.response.UserPrincipal;
import kr.me.seesaw.message.CmsMessageSource;
import kr.me.seesaw.response.UserResponse;
import kr.me.seesaw.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

    private final CmsMessageSource messageSource;







    public void send(String from, String[] to, String title, String viewName, Map<String, String> values) {
        log.info("메일을 전송합니다.\n발신자: {}\n수신자: {}\n제목: {}", from, to, title);
    }





}
