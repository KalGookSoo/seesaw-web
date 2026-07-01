package kr.me.seesaw.web.oauth2;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.client.provider.naver")
public class NaverOAuthClientProviderProperties {

    private String authorizationUri;

    private String tokenUri;

    private String userInfoUri;

    private String userNameAttribute;

    @PostConstruct
    public void validate() {
        Assert.notNull(authorizationUri, "spring.security.oauth2.client.provider.naver.authorization-uri 설정은 필수입니다.");
        Assert.notNull(tokenUri, "spring.security.oauth2.client.provider.naver.token-uri 설정은 필수입니다.");
        Assert.notNull(userInfoUri, "spring.security.oauth2.client.provider.naver.user-info-uri 설정은 필수입니다.");
        Assert.notNull(userNameAttribute, "spring.security.oauth2.client.provider.naver.user-name-attribute 설정은 필수입니다.");
    }

}
