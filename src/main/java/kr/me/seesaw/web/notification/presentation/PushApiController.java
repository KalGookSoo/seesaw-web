package kr.me.seesaw.web.notification.presentation;

import jakarta.validation.Valid;
import kr.me.seesaw.web.notification.dto.CreateWebPushSubscriptionRequest;
import kr.me.seesaw.web.notification.dto.DeleteWebPushSubscriptionRequest;
import kr.me.seesaw.web.SeesawWebProperties;
import kr.me.seesaw.web.notification.dto.WebPushSubscriptionResponse;
import kr.me.seesaw.web.notification.WebPushSubscriptionService;
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

    private final SeesawWebProperties seesawWebProperties;

    private final WebPushSubscriptionService webPushSubscriptionService;

    @GetMapping("/vapid-public-key")
    public ResponseEntity<Map<String, String>> getVapidPublicKey() {
        String publicKey = seesawWebProperties.getWebPush().getVapid().getPublicKey();
        return ResponseEntity.ok(Map.of("publicKey", publicKey));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/subscriptions")
    public ResponseEntity<WebPushSubscriptionResponse> subscribe(
            @Valid @RequestBody CreateWebPushSubscriptionRequest command,
            @RequestHeader(HttpHeaders.USER_AGENT) String userAgent
    ) {
        WebPushSubscriptionResponse subscription = webPushSubscriptionService.subscribe(command, userAgent);
        return ResponseEntity.ok(subscription);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/subscriptions")
    public ResponseEntity<Void> unsubscribe(@Valid @RequestBody DeleteWebPushSubscriptionRequest command) {
        webPushSubscriptionService.unsubscribe(command);
        return ResponseEntity.noContent().build();
    }

}
