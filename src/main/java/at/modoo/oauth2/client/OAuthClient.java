package at.modoo.oauth2.client;

import at.modoo.oauth2.provider.OAuth2UserDetail;
import org.springframework.http.ResponseEntity;

public interface OAuthClient {

    String getRedirectUri();

    String getAccessToken(String code);

    OAuth2UserDetail authenticate(String accessToken);

    default void verify(ResponseEntity<?> responseEntity) {
//        Assert.isTrue(responseEntity.getStatusCode().is2xxSuccessful(), "OAuth 서버 응답 오류");
//        Assert.state(responseEntity.getStatusCode().is5xxServerError(), "OAuth 서버 오류");
//        Assert.state(responseEntity.getBody() != null, "OAuth 서버 응답 오류");
    }

}
