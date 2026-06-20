package kr.me.seesaw.controller;

import jakarta.validation.Valid;
import kr.me.seesaw.dto.command.SendHelpdeskCommand;
import kr.me.seesaw.dto.command.SendReportCommand;
import kr.me.seesaw.service.MailService;
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

    private final MailService mailService;

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @PostMapping("/send-to-report")
    public ResponseEntity<Void> sendToReport(@Valid @RequestBody SendReportCommand command) {
        mailService.sendToReport(command.siteId(), command.title(), command.title());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @PostMapping("/send-to-helpdesk")
    public ResponseEntity<Void> sendToHelpdesk(@Valid @RequestBody SendHelpdeskCommand command) {
        mailService.sendToHelpdesk(command.title(), command.title());
        return ResponseEntity.ok().build();
    }

}
