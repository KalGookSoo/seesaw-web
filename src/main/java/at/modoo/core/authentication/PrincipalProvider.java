package at.modoo.core.authentication;

import org.springframework.security.core.Authentication;

public interface PrincipalProvider {

    Authentication getAuthentication();

}
