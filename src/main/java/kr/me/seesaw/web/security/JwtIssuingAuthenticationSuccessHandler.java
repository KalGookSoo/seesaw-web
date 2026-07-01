package kr.me.seesaw.web.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.me.seesaw.api.security.dto.JsonWebToken;
import kr.me.seesaw.framework.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;

@RequiredArgsConstructor
public class JwtIssuingAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final int ACCESS_TOKEN_MAX_AGE = 60 * 60;

    private static final int REFRESH_TOKEN_MAX_AGE = 60 * 60 * 24 * 14;

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws ServletException, IOException {
        if (authentication.getPrincipal() instanceof UserPrincipal principal) {
            Collection<String> authorities = principal.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            JsonWebToken token = jwtTokenProvider.generateTokenInfo(principal.getUsername(), principal.getUser().getId(), authorities);
            response.addCookie(createCookie("SEESAW_ACCESS_TOKEN", token.accessToken(), ACCESS_TOKEN_MAX_AGE));
            response.addCookie(createCookie("SEESAW_REFRESH_TOKEN", token.refreshToken(), REFRESH_TOKEN_MAX_AGE));
            response.setHeader("X-SEESAW-ACCESS-TOKEN", token.accessToken());
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setSecure(true);
        cookie.setHttpOnly(false);
        cookie.setAttribute("SameSite", "Lax");
        return cookie;
    }

}
