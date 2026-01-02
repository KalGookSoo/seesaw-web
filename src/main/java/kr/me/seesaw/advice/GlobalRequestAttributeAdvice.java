package kr.me.seesaw.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@ControllerAdvice
public class GlobalRequestAttributeAdvice {

    private static final String REQUEST_URI_BUILDER = "REQUEST_URI_BUILDER";

    private static final String ACTIVE_PROFILES = "ACTIVE_PROFILES";

    private final Environment environment;

    public GlobalRequestAttributeAdvice(Environment environment) {
        this.environment = environment;
    }

    @ModelAttribute
    public void addAttributes(HttpServletRequest request, Model model) {
        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromRequest(request);
        model.addAttribute(REQUEST_URI_BUILDER, uriBuilder);

        model.addAttribute(ACTIVE_PROFILES, List.of(environment.getActiveProfiles()));
    }

}
