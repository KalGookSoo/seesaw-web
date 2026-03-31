package kr.me.seesaw.config;

import kr.me.seesaw.core.authentication.IpAddressExtractor;
import kr.me.seesaw.core.authentication.PrincipalProvider;
import kr.me.seesaw.core.authentication.SecurityPrincipalProvider;
import kr.me.seesaw.core.authentication.ServletIpAddressExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PrincipalConfig {

    @Bean
    public PrincipalProvider principalProvider() {
        return new SecurityPrincipalProvider();
    }

    @Bean
    public IpAddressExtractor ipAddressExtractor() {
        return new ServletIpAddressExtractor();
    }

}
