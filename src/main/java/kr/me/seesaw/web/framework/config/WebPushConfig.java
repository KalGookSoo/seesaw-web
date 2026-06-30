package kr.me.seesaw.web.framework.config;

import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.GeneralSecurityException;
import java.security.Security;

@Configuration
public class WebPushConfig {

    @Bean
    public PushService pushService(SeesawWebProperties properties) throws GeneralSecurityException {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        SeesawWebProperties.Vapid vapid = properties.getWebPush().getVapid();
        return new PushService(vapid.getPublicKey(), vapid.getPrivateKey(), vapid.getSubject());
    }

}
