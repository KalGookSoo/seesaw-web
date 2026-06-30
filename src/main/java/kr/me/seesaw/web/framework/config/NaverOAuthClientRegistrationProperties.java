package kr.me.seesaw.web.framework.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.naver")
public class NaverOAuthClientRegistrationProperties {

    private String clientId;

    private String clientSecret;

    private String redirectUri;

    private String authorizationGrantType;

    private String scope;

    private String clientName;

    @PostConstruct
    public void validate() {
        Assert.notNull(clientId, "spring.security.oauth2.client.registration.naver.client-id 설정은 필수입니다.");
        Assert.notNull(clientSecret, "spring.security.oauth2.client.registration.naver.client-secret 설정은 필수입니다.");
        Assert.notNull(redirectUri, "spring.security.oauth2.client.registration.naver.redirect-uri 설정은 필수입니다.");
        Assert.notNull(authorizationGrantType, "spring.security.oauth2.client.registration.naver.authorization-grant-type 설정은 필수입니다.");
        Assert.notNull(scope, "spring.security.oauth2.client.registration.naver.scope 설정은 필수입니다.");
        Assert.notNull(clientName, "spring.security.oauth2.client.registration.naver.client-name 설정은 필수입니다.");
    }

}
