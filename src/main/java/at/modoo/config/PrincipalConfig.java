package at.modoo.config;

import at.modoo.core.authentication.PrincipalProvider;
import at.modoo.core.authentication.SecurityPrincipalProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PrincipalConfig {

    @Bean
    public PrincipalProvider principalProvider() {
        return new SecurityPrincipalProvider();
    }

}
