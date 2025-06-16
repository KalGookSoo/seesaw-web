package kr.me.seesaw.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/session")
public class SessionApiController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("id", session.getId());

        Map<String, Object> attributes = new HashMap<>();
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            attributes.put(attributeName, session.getAttribute(attributeName));
        }
        responseBody.put("attributes", attributes);

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/refresh")
    public ResponseEntity<Void> refresh(HttpServletRequest request) {
        HttpSession session = request.getSession();
        int maxInactiveInterval = session.getMaxInactiveInterval();
        session.setMaxInactiveInterval(maxInactiveInterval);
        return ResponseEntity.ok().build();
    }

}