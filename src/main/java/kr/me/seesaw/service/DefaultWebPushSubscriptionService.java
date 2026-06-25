package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateWebPushSubscriptionCommand;
import kr.me.seesaw.command.DeleteWebPushSubscriptionCommand;
import kr.me.seesaw.core.authentication.PrincipalProvider;
import kr.me.seesaw.domain.Site;
import kr.me.seesaw.domain.User;
import kr.me.seesaw.domain.WebPushSubscription;
import kr.me.seesaw.dto.model.UserPrincipal;
import kr.me.seesaw.model.WebPushSubscriptionModel;
import kr.me.seesaw.repository.SiteRepository;
import kr.me.seesaw.repository.UserRepository;
import kr.me.seesaw.repository.WebPushSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.authorization.AuthorizationDeniedException;
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
    public WebPushSubscriptionModel subscribe(CreateWebPushSubscriptionCommand command, String userAgent) {
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

        return new WebPushSubscriptionModel(webPushSubscriptionRepository.save(subscription));
    }

    @Override
    public void unsubscribe(DeleteWebPushSubscriptionCommand command) {
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
    public List<WebPushSubscriptionModel> findAllEnabledBySiteId(String siteId) {
        return webPushSubscriptionRepository.findAllBySiteIdAndEnabledTrue(siteId)
                .stream()
                .map(WebPushSubscriptionModel::new)
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
                .map(kr.me.seesaw.model.UserModel::getId);

        return userId.or(() -> userRepository.findByUsername(authentication.getName()).map(User::getId))
                .orElseThrow(() -> new AuthorizationDeniedException("계정 정보를 찾을 수 없습니다."));
    }

}
