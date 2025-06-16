package kr.me.seesaw.service;

import kr.me.seesaw.domain.Role;
import kr.me.seesaw.domain.User;
import kr.me.seesaw.domain.UserPrincipal;
import kr.me.seesaw.domain.vo.Email;
import kr.me.seesaw.domain.vo.RoleName;
import kr.me.seesaw.oauth2.provider.NaverUserDetail;
import kr.me.seesaw.oauth2.provider.OAuth2UserDetail;
import kr.me.seesaw.repository.RoleRepository;
import kr.me.seesaw.repository.UserRepository;
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
                    Role role = Role.create(user.getId(), siteId, RoleName.ROLE_USER.name(), RoleName.ROLE_USER.getDescription());
                    roleRepository.save(role);
                    user.addRole(role);
                    return new UserPrincipal(user, oAuth2User.getAttributes());
                });

    }
}
