package at.modoo.core.authentication;

import org.springframework.security.core.Authentication;

public class AnonymousPrincipalProvider implements PrincipalProvider {

    @Override
    public Authentication getAuthentication() {
        return null;
    }

}
