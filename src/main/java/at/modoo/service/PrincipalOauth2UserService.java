package at.modoo.service;

import at.modoo.entity.Authority;
import at.modoo.entity.Email;
import at.modoo.entity.User;
import at.modoo.entity.UserPrincipal;
import at.modoo.oauth2.provider.NaverUserDetail;
import at.modoo.oauth2.provider.OAuth2UserDetail;
import at.modoo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        if (!"naver".equals(userRequest.getClientRegistration().getRegistrationId())) {
            throw new OAuth2AuthenticationException("계정 인증 실패");
        }
        OAuth2UserDetail oAuth2UserDetail = new NaverUserDetail(oAuth2User.getAttributes());
        String provider = oAuth2UserDetail.getProvider();
        String providerId = oAuth2UserDetail.getProviderId();
        String username = provider + "_" + providerId;
        String email = oAuth2UserDetail.getEmail();
        return userRepository.findByUsername(username)
                .map(user -> new UserPrincipal(user, oAuth2User.getAttributes()))
                .orElseGet(() -> {
                    User user = User.create(username, new Email(email.split("@")[0], email.split("@")[1]));
                    Authority authority = Authority.create("ROLE_USER", user);
                    user.addAuthority(authority);
                    return new UserPrincipal(userRepository.save(user), oAuth2User.getAttributes());
                });

    }
}
