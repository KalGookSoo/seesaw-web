package kr.me.seesaw.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "kr.me.seesaw")
public class SeesawProperties {

    private String filepath;

    private String apiHost;

}
