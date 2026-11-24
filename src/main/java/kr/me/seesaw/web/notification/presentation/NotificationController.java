package kr.me.seesaw.web.notification.presentation;

import kr.me.seesaw.core.domain.notification.NotificationType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notifications")
@PreAuthorize("isAuthenticated()")
public class NotificationController {

    @GetMapping
    public String getInbox() {
        return "notifications/index";
    }

    @GetMapping("/settings")
    public String getSettings(Model model) {
        model.addAttribute("notificationTypes", NotificationType.values());
        return "notifications/settings";
    }

}
