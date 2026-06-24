package kr.me.seesaw.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.me.seesaw.command.CreateWebPushSubscriptionCommand;
import kr.me.seesaw.command.DeleteWebPushSubscriptionCommand;
import kr.me.seesaw.config.SeesawProperties;
import kr.me.seesaw.model.WebPushSubscriptionModel;
import kr.me.seesaw.service.WebPushSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/push")
public class PushApiController {

    private final SeesawProperties seesawProperties;

    private final WebPushSubscriptionService webPushSubscriptionService;

    @GetMapping("/vapid-public-key")
    public ResponseEntity<Map<String, String>> getVapidPublicKey() {
        String publicKey = seesawProperties.getWebPush().getVapid().getPublicKey();
        return ResponseEntity.ok(Map.of("publicKey", publicKey));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/subscriptions")
    public ResponseEntity<WebPushSubscriptionModel> subscribe(
            @Valid @RequestBody CreateWebPushSubscriptionCommand command,
            HttpServletRequest request
    ) {
        WebPushSubscriptionModel subscription = webPushSubscriptionService.subscribe(command, request.getHeader(HttpHeaders.USER_AGENT));
        return ResponseEntity.ok(subscription);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/subscriptions")
    public ResponseEntity<Void> unsubscribe(@Valid @RequestBody DeleteWebPushSubscriptionCommand command) {
        webPushSubscriptionService.unsubscribe(command);
        return ResponseEntity.noContent().build();
    }

}
