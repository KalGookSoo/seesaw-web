package at.modoo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import at.modoo.oauth2.client.NaverOAuthClient;
import at.modoo.oauth2.properties.NaverOAuthProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OAuthConfig {

    private final NaverOAuthProperties naverOAuthProperties;

    public OAuthConfig(NaverOAuthProperties naverOAuthProperties) {
        this.naverOAuthProperties = naverOAuthProperties;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public NaverOAuthClient naverOAuthClient(
            @Qualifier("restTemplate") RestTemplate restTemplate,
            @Qualifier("objectMapper") ObjectMapper objectMapper
    ) {
        return new NaverOAuthClient(
                restTemplate,
                objectMapper,
                naverOAuthProperties.getClientId(),
                naverOAuthProperties.getClientSecret(),
                naverOAuthProperties.getRedirectUri()
        );
    }

}
