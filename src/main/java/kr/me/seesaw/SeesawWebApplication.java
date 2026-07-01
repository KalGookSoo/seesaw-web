package kr.me.seesaw;

import kr.me.seesaw.core.support.file.LocalFileManager;
import kr.me.seesaw.framework.context.I18nConfig;
import kr.me.seesaw.framework.context.properties.SeesawApiProperties;
import kr.me.seesaw.framework.security.SecurityAuditorAware;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "kr.me.seesaw.core",
                "kr.me.seesaw.api",
                "kr.me.seesaw.web",
        }
)
@Import({
        I18nConfig.class,
        BCryptPasswordEncoder.class,
        SecurityAuditorAware.class,
        SeesawApiProperties.class,
        LocalFileManager.class
})
public class SeesawWebApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SeesawWebApplication.class);
        application.addListeners(new ApplicationPidFileWriter());
        application.run(args);
    }

}
