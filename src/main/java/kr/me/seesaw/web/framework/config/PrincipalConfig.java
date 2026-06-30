package kr.me.seesaw.web.framework.config;

import kr.me.seesaw.core.support.audit.IpAddressExtractor;
import kr.me.seesaw.core.support.audit.ServletIpAddressExtractor;
import kr.me.seesaw.core.support.authentication.PrincipalProvider;
import kr.me.seesaw.core.support.authentication.SecurityPrincipalProvider;
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
