package kr.me.seesaw.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "kr.me.seesaw")
public class SeesawProperties {

    private String filepath;

    private String apiHost;

    private WebPush webPush = new WebPush();

    @PostConstruct
    public void validate() {
        Assert.hasText(filepath, "kr.me.seesaw.filepath 설정은 필수입니다.");
        Assert.hasText(apiHost, "kr.me.seesaw.api-host 설정은 필수입니다.");
        Assert.notNull(webPush, "kr.me.seesaw.web-push 설정은 필수입니다.");
        webPush.validate();
    }

    @Setter
    @Getter
    public static class WebPush {

        private Vapid vapid = new Vapid();

        private void validate() {
            Assert.notNull(vapid, "kr.me.seesaw.web-push.vapid 설정은 필수입니다.");
            vapid.validate();
        }

    }

    @Setter
    @Getter
    public static class Vapid {

        private String publicKey;

        private String privateKey;

        private String subject;

        private void validate() {
            Assert.hasText(publicKey, "kr.me.seesaw.web-push.vapid.public-key 설정은 필수입니다.");
            Assert.hasText(privateKey, "kr.me.seesaw.web-push.vapid.private-key 설정은 필수입니다.");
            Assert.hasText(subject, "kr.me.seesaw.web-push.vapid.subject 설정은 필수입니다.");
        }

    }

}
