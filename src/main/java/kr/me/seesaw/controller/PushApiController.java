package kr.me.seesaw.controller;

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
import org.springframework.web.bind.annotation.*;

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
            @RequestHeader(HttpHeaders.USER_AGENT) String userAgent
    ) {
        WebPushSubscriptionModel subscription = webPushSubscriptionService.subscribe(command, userAgent);
        return ResponseEntity.ok(subscription);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/subscriptions")
    public ResponseEntity<Void> unsubscribe(@Valid @RequestBody DeleteWebPushSubscriptionCommand command) {
        webPushSubscriptionService.unsubscribe(command);
        return ResponseEntity.noContent().build();
    }

}
