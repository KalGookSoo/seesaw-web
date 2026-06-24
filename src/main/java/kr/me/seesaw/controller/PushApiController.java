package kr.me.seesaw.controller;

import kr.me.seesaw.config.SeesawProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/push")
public class PushApiController {

    private final SeesawProperties seesawProperties;

    @GetMapping("/vapid-public-key")
    public ResponseEntity<Map<String, String>> getVapidPublicKey() {
        String publicKey = seesawProperties.getWebPush().getVapid().getPublicKey();
        return ResponseEntity.ok(Map.of("publicKey", publicKey));
    }

}
