package at.modoo.controller;

import at.modoo.oauth2.client.NaverOAuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class SignController {

    private final NaverOAuthClient naverOAuthClient;

    private final DefaultOAuth2UserService defaultOAuth2UserService;

    private final ClientRegistrationRepository clientRegistrationRepository;

    @RequestMapping(value = "/login/oauth2/code/naver", method = {RequestMethod.GET, RequestMethod.POST})
    public String oauth2NaverCallback(@RequestParam("code") String code) {
//        String accessToken = naverOAuthClient.getAccessToken(code);
//        OAuth2UserDetail userDetail = naverOAuthClient.authenticate(accessToken);
//        defaultOAuth2UserService.loadUser()
        return "redirect:/";
    }

}
