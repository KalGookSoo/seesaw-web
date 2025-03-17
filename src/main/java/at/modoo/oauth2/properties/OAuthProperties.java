package at.modoo.oauth2.properties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class OAuthProperties {

    private String clientId;

    private String clientSecret;

    private String redirectUri;

}
