package kr.me.seesaw.web.notification.application;

import kr.me.seesaw.web.notification.WebPushSubscriptionService;
import kr.me.seesaw.web.notification.dto.CreateWebPushSubscriptionRequest;
import kr.me.seesaw.web.notification.dto.DeleteWebPushSubscriptionRequest;
import kr.me.seesaw.web.notification.dto.WebPushSubscriptionResponse;
import kr.me.seesaw.api.user.dto.UserResponse;
import kr.me.seesaw.core.domain.notification.WebPushSubscription;
import kr.me.seesaw.core.domain.notification.WebPushSubscriptionRepository;
import kr.me.seesaw.core.domain.site.Site;
import kr.me.seesaw.core.domain.site.SiteRepository;
import kr.me.seesaw.core.domain.user.User;
import kr.me.seesaw.core.domain.user.UserRepository;
import kr.me.seesaw.core.support.authentication.PrincipalProvider;
import kr.me.seesaw.web.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class DefaultWebPushSubscriptionService implements WebPushSubscriptionService {

    private final WebPushSubscriptionRepository webPushSubscriptionRepository;

    private final SiteRepository siteRepository;

    private final UserRepository userRepository;

    private final PrincipalProvider principalProvider;

    @Override
    public WebPushSubscriptionResponse subscribe(CreateWebPushSubscriptionRequest command, String userAgent) {
        Authentication authentication = getAuthentication();
        String userId = getUserId(authentication);
        Site site = siteRepository.getReferenceById(command.getSiteId());
        User user = userRepository.getReferenceById(userId);

        WebPushSubscription subscription = webPushSubscriptionRepository.findByEndpoint(command.getEndpoint())
                .orElseGet(WebPushSubscription::new);
        subscription.update(
                site,
                user,
                command.getEndpoint(),
                command.getKeys().getP256dh(),
                command.getKeys().getAuth(),
                userAgent
        );

        return new WebPushSubscriptionResponse(webPushSubscriptionRepository.save(subscription));
    }

    @Override
    public void unsubscribe(DeleteWebPushSubscriptionRequest command) {
        Authentication authentication = getAuthentication();
        webPushSubscriptionRepository.findBySiteIdAndUserUsernameAndEndpoint(
                        command.getSiteId(),
                        authentication.getName(),
                        command.getEndpoint()
                )
                .ifPresent(subscription -> {
                    subscription.disable();
                    webPushSubscriptionRepository.save(subscription);
                });
    }

    @Transactional(readOnly = true)
    @Override
    public List<WebPushSubscriptionResponse> findAllEnabledBySiteId(String siteId) {
        return webPushSubscriptionRepository.findAllBySiteIdAndEnabledTrue(siteId)
                .stream()
                .map(WebPushSubscriptionResponse::new)
                .toList();
    }

    private Authentication getAuthentication() {
        Authentication authentication = principalProvider.getAuthentication();
        Assert.notNull(authentication, "인증 정보는 NULL이 될 수 없습니다.");
        Assert.hasText(authentication.getName(), "인증 계정명은 비어 있을 수 없습니다.");
        return authentication;
    }

    private String getUserId(Authentication authentication) {
        Optional<String> userId = Optional.ofNullable(authentication.getPrincipal())
                .filter(UserPrincipal.class::isInstance)
                .map(UserPrincipal.class::cast)
                .map(UserPrincipal::getUser)
                .map(UserResponse::getId);

        return userId.or(() -> userRepository.findByUsername(authentication.getName()).map(User::getId))
                .orElseThrow(() -> new AuthorizationDeniedException("계정 정보를 찾을 수 없습니다."));
    }

}
