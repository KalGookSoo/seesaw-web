package at.modoo.oauth2.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.naver")
public class NaverOAuthProperties extends OAuthProperties {

}
