package kr.me.seesaw.web.styleguide.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/style-guide/editorial")
public class EditorialStyleGuideController {

    @GetMapping("/components")
    public String getComponents() {
        return "style-guide/editorial/components";
    }

}
