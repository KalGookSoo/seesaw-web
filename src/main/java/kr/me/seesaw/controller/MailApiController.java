package kr.me.seesaw.controller;

import jakarta.validation.Valid;
import kr.me.seesaw.dto.command.SendMailCommand;
import kr.me.seesaw.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailApiController {

    private final MailService mailService;

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @PostMapping("/helpdesk")
    public ResponseEntity<Map<String, String>> sendHelpdesk(@Valid @RequestBody SendMailCommand command) {
        mailService.sendToHelp(command.title(), command.title());
        return ResponseEntity.ok(Map.of("message", "success"));
    }

}
