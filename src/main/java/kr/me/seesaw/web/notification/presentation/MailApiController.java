package kr.me.seesaw.web.notification.presentation;

import jakarta.validation.Valid;
import kr.me.seesaw.web.notification.dto.SendMailRequest;
import kr.me.seesaw.web.notification.HelpdeskService;
import kr.me.seesaw.web.notification.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailApiController {

    private final ReportService reportService;

    private final HelpdeskService helpdeskService;

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @PostMapping("/send-to-report")
    public ResponseEntity<Void> sendToReport(@Valid @RequestBody SendMailRequest command) {
        reportService.sendToReport(command.siteId(), command.title(), command.content());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @PostMapping("/send-to-helpdesk")
    public ResponseEntity<Void> sendToHelpdesk(@Valid @RequestBody SendMailRequest command) {
        helpdeskService.sendToHelpdesk(command.siteId(), command.title(), command.content());
        return ResponseEntity.ok().build();
    }

}
