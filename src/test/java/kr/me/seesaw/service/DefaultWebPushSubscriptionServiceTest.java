package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateWebPushSubscriptionCommand;
import kr.me.seesaw.command.DeleteWebPushSubscriptionCommand;
import kr.me.seesaw.config.TestDataInitializerConfig;
import kr.me.seesaw.domain.Site;
import kr.me.seesaw.domain.WebPushSubscription;
import kr.me.seesaw.model.WebPushSubscriptionModel;
import kr.me.seesaw.repository.SiteRepository;
import kr.me.seesaw.repository.WebPushSubscriptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Import(TestDataInitializerConfig.class)
@SpringBootTest
@Transactional
class DefaultWebPushSubscriptionServiceTest {

    @Autowired
    private WebPushSubscriptionService webPushSubscriptionService;

    @Autowired
    private WebPushSubscriptionRepository webPushSubscriptionRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("구독 생성 요청 시 테스트 데이터의 사이트와 계정 기준으로 구독 정보를 저장합니다.")
    void subscribeShouldCreateSubscription() {
        Site site = findTestSite();
        CreateWebPushSubscriptionCommand command = createCommand(site.getId(), "endpoint-create");

        WebPushSubscriptionModel model = webPushSubscriptionService.subscribe(command, "user-agent");

        WebPushSubscription subscription = webPushSubscriptionRepository.findByEndpoint("endpoint-create")
                .orElseThrow();
        assertNotNull(model);
        assertEquals(subscription.getId(), model.getId());
        assertEquals(site.getId(), subscription.getSiteId());
        assertEquals("user", subscription.getUser().getUsername());
        assertEquals("endpoint-create", subscription.getEndpoint());
        assertEquals("p256dh", subscription.getP256dh());
        assertEquals("auth", subscription.getAuth());
        assertEquals("user-agent", subscription.getUserAgent());
        assertTrue(subscription.isEnabled());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("기존 endpoint로 구독 요청 시 저장된 구독 정보를 갱신합니다.")
    void subscribeShouldUpdateExistingSubscription() {
        Site site = findTestSite();
        CreateWebPushSubscriptionCommand firstCommand = createCommand(site.getId(), "endpoint-update");
        webPushSubscriptionService.subscribe(firstCommand, "old-agent");

        CreateWebPushSubscriptionCommand updateCommand = createCommand(site.getId(), "endpoint-update", "new-p256dh", "new-auth");
        WebPushSubscriptionModel model = webPushSubscriptionService.subscribe(updateCommand, "new-agent");

        WebPushSubscription subscription = webPushSubscriptionRepository.findByEndpoint("endpoint-update")
                .orElseThrow();
        assertEquals(subscription.getId(), model.getId());
        assertEquals("new-p256dh", subscription.getP256dh());
        assertEquals("new-auth", subscription.getAuth());
        assertEquals("new-agent", subscription.getUserAgent());
        assertTrue(subscription.isEnabled());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("구독 삭제 요청 시 구독을 비활성화합니다.")
    void unsubscribeShouldDisableSubscription() {
        Site site = findTestSite();
        CreateWebPushSubscriptionCommand createCommand = createCommand(site.getId(), "endpoint-delete");
        webPushSubscriptionService.subscribe(createCommand, "user-agent");

        DeleteWebPushSubscriptionCommand deleteCommand = new DeleteWebPushSubscriptionCommand();
        deleteCommand.setSiteId(site.getId());
        deleteCommand.setEndpoint("endpoint-delete");
        webPushSubscriptionService.unsubscribe(deleteCommand);

        WebPushSubscription subscription = webPushSubscriptionRepository.findByEndpoint("endpoint-delete")
                .orElseThrow();
        assertFalse(subscription.isEnabled());
    }

    private Site findTestSite() {
        return siteRepository.findByDomainName("test1.local")
                .orElseThrow(() -> new NoSuchElementException("테스트 사이트를 찾을 수 없습니다."));
    }

    private CreateWebPushSubscriptionCommand createCommand(String siteId, String endpoint) {
        return createCommand(siteId, endpoint, "p256dh", "auth");
    }

    private CreateWebPushSubscriptionCommand createCommand(String siteId, String endpoint, String p256dh, String auth) {
        CreateWebPushSubscriptionCommand.Keys keys = new CreateWebPushSubscriptionCommand.Keys();
        keys.setP256dh(p256dh);
        keys.setAuth(auth);

        CreateWebPushSubscriptionCommand command = new CreateWebPushSubscriptionCommand();
        command.setSiteId(siteId);
        command.setEndpoint(endpoint);
        command.setKeys(keys);
        return command;
    }

}
