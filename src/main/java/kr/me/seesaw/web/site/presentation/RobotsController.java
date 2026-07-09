package kr.me.seesaw.web.site.presentation;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Duration;

@Controller
public class RobotsController {

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getRobots() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .cacheControl(CacheControl.maxAge(Duration.ofHours(1)))
                .body(toRobotsTxt());
    }

    private String toRobotsTxt() {
        return """
                User-agent: *
                Allow: /
                Allow: /api/sitemap.xml
                Disallow: /api/
                Disallow: /admin/
                Disallow: /error/
                
                # Allow Google bots full access
                User-agent: Googlebot
                Allow: /
                
                # Allow Naver bots full access
                User-agent: Yeti
                Allow: /
                
                # Sitemap location
                Sitemap: %s
                """.formatted(getSitemapUrl());
    }

    private String getSitemapUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/sitemap.xml")
                .toUriString();
    }

}
