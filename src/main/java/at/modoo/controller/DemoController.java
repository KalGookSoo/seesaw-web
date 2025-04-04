package at.modoo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/demo")
@Controller
public class DemoController {

    @GetMapping("/{viewName}")
    public String demo(@PathVariable("viewName") String viewName) {
        return "demo/" + viewName;
    }

}
