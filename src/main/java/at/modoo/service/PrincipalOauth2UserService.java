package at.modoo.service;

import at.modoo.model.Role;
import at.modoo.model.User;
import at.modoo.model.UserPrincipal;
import at.modoo.model.vo.Email;
import at.modoo.oauth2.provider.NaverUserDetail;
import at.modoo.oauth2.provider.OAuth2UserDetail;
import at.modoo.repository.RoleRepository;
import at.modoo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final String domainName;

    private final SiteService siteService;

    public PrincipalOauth2UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            @Value("${site.domain.name}") String domainName,
            SiteService siteService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.domainName = domainName;
        this.siteService = siteService;
    }

    // TODO 해당 사이트랑 계정 연결하여 사이트에 종속된 권한만 인증주체에 바인딩한다.
    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        if (!"naver".equals(userRequest.getClientRegistration().getRegistrationId())) {
            throw new OAuth2AuthenticationException("계정 인증 실패");
        }
        OAuth2UserDetail oAuth2UserDetail = new NaverUserDetail(oAuth2User.getAttributes());
        String email = oAuth2UserDetail.getEmail();
        String username = email.split("@")[0];
        String siteId = siteService.getSite(domainName).getId();
        return userRepository.findByUsername(username)
                .map(user -> {
                    List<Role> roles = roleRepository.findAllByReferenceId(user.getId()).stream().filter(role -> siteId.equals(role.getSiteId())).toList();
                    roles.forEach(user::addRole);
                    return new UserPrincipal(user, oAuth2User.getAttributes());
                })
                .orElseGet(() -> {
                    User user = User.create(username, new Email(email.split("@")[0], email.split("@")[1]));
                    userRepository.save(user);
                    Role role = Role.create(user.getId(), siteId, "ROLE_USER", "일반사용자");
                    roleRepository.save(role);
                    user.addRole(role);
                    return new UserPrincipal(user, oAuth2User.getAttributes());
                });

    }
}
