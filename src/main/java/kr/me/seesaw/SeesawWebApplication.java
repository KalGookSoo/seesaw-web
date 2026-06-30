package kr.me.seesaw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "kr.me.seesaw.core",
                "kr.me.seesaw.api",
                "kr.me.seesaw.web",
        }
)
public class SeesawWebApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SeesawWebApplication.class);
        application.addListeners(new ApplicationPidFileWriter());
        application.run(args);
    }

}
