package at.modoo.config;

import at.modoo.command.CreateUserCommand;
import at.modoo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;

import java.util.NoSuchElementException;

@Profile("local")
@Configuration
public class ApplicationRunner implements CommandLineRunner, ApplicationListener<ApplicationStartedEvent> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserService userService;

    public ApplicationRunner(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationStartedEvent event) {
        logger.info("\n\nApplication started\n\n");
    }

    @Override
    public void run(String... args) {
        createUserCommand("admin", "관리자", true);
        createUserCommand("tester", "테스터", false);
    }

    private void createUserCommand(String username, String name, boolean isAdministrator) {
        CreateUserCommand command = new CreateUserCommand(
                username,
                "12341234",
                name,
                "ga.miro3721",
                "gmail.com",
                "010",
                "1234",
                "5678"
        );
        try {
            userService.findByUsername(command.username());
        } catch (NoSuchElementException e) {
            if (isAdministrator) {
                userService.createAdmin(command);
                return;
            }
            userService.createUser(command);
        }
    }

}
