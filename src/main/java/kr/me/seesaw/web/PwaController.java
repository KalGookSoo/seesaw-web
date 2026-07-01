package kr.me.seesaw.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.me.seesaw.api.site.SiteContext;
import kr.me.seesaw.core.domain.site.SiteColor;
import kr.me.seesaw.api.site.dto.SiteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class PwaController {

    private static final MediaType MANIFEST_MEDIA_TYPE = MediaType.valueOf("application/manifest+json");

    private static final MediaType JAVASCRIPT_MEDIA_TYPE = MediaType.valueOf("application/javascript");

    private static final String SERVICE_WORKER_ALLOWED_HEADER = "Service-Worker-Allowed";

    private final SiteContext siteContext;

    private final ObjectMapper objectMapper;

    @GetMapping(value = "/manifest.webmanifest", produces = "application/manifest+json")
    public ResponseEntity<Map<String, Object>> getManifest() {
        SiteResponse site = siteContext.getSite();
        Map<String, Object> manifest = new LinkedHashMap<>();
        manifest.put("id", site.getId());
        manifest.put("name", site.getName());
        manifest.put("short_name", site.getName());
        manifest.put("description", site.getDescription());
        manifest.put("start_url", "/");
        manifest.put("scope", "/");
        manifest.put("display", "standalone");
        manifest.put("background_color", getBackgroundColor(site));
        manifest.put("theme_color", getThemeColor(site));
        manifest.put("icons", List.of(
                Map.of(
                        "src", "/favicon.png",
                        "sizes", "320x320",
                        "type", "image/png"
                ),
                Map.of(
                        "src", "/pwa-icon-192.png",
                        "sizes", "192x192",
                        "type", "image/png"
                ),
                Map.of(
                        "src", "/pwa-icon-512.png",
                        "sizes", "512x512",
                        "type", "image/png",
                        "purpose", "any maskable"
                )
        ));

        return ResponseEntity.ok()
                .contentType(MANIFEST_MEDIA_TYPE)
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(10)))
                .body(manifest);
    }

    @GetMapping(value = "/sw.js", produces = "application/javascript")
    public ResponseEntity<String> getServiceWorker() {
        SiteResponse site = siteContext.getSite();
        String siteId = site.getId();
        String cacheName = "seesaw-" + siteId + "-v1";
        String serviceWorker = """
                const SITE_ID = %s;
                const CACHE_NAME = %s;
                const CACHEABLE_PATHS = [
                  "/favicon.png",
                  "/pwa-icon-192.png",
                  "/pwa-icon-512.png",
                  "/styles/",
                  "/scripts/",
                  "/images/",
                  "/fonts/"
                ];
                
                self.addEventListener("install", event => {
                  self.skipWaiting();
                });
                
                self.addEventListener("activate", event => {
                  event.waitUntil((async () => {
                    const cacheNames = await caches.keys();
                    await Promise.all(
                      cacheNames
                        .filter(cacheName => cacheName.startsWith(`seesaw-${SITE_ID}-`) && cacheName !== CACHE_NAME)
                        .map(cacheName => caches.delete(cacheName))
                    );
                    await self.clients.claim();
                  })());
                });
                
                self.addEventListener("fetch", event => {
                  const { request } = event;
                  if (request.method !== "GET") {
                    return;
                  }
                
                  const url = new URL(request.url);
                  if (url.origin !== self.location.origin || !CACHEABLE_PATHS.some(path => url.pathname.startsWith(path))) {
                    return;
                  }
                
                  event.respondWith((async () => {
                    const cache = await caches.open(CACHE_NAME);
                    try {
                      const response = await fetch(request);
                      if (response.ok) {
                        cache.put(request, response.clone());
                      }
                      return response;
                    } catch (error) {
                      const cachedResponse = await cache.match(request);
                      if (cachedResponse) {
                        return cachedResponse;
                      }
                      throw error;
                    }
                  })());
                });
                
                self.addEventListener("push", event => {
                  if (!event.data) {
                    return;
                  }
                
                  let data = {};
                  try {
                    data = event.data.json();
                  } catch (error) {
                    data = { body: event.data.text() };
                  }
                  const title = data.title || "Seesaw";
                  const options = {
                    body: data.body || "",
                    icon: data.icon || "/favicon.png",
                    badge: data.badge || "/favicon.png",
                    data: {
                      url: data.url || "/"
                    }
                  };
                
                  event.waitUntil(self.registration.showNotification(title, options));
                });
                
                self.addEventListener("notificationclick", event => {
                  event.notification.close();
                  const targetUrl = new URL(event.notification.data?.url || "/", self.location.origin).href;
                
                  event.waitUntil((async () => {
                    const clientsList = await clients.matchAll({ type: "window", includeUncontrolled: true });
                    const client = clientsList.find(client => client.url === targetUrl);
                    if (client) {
                      return client.focus();
                    }
                    return clients.openWindow(targetUrl);
                  })());
                });
                """.formatted(toJson(siteId), toJson(cacheName));

        return ResponseEntity.ok()
                .contentType(JAVASCRIPT_MEDIA_TYPE)
                .cacheControl(CacheControl.noCache())
                .header(SERVICE_WORKER_ALLOWED_HEADER, "/")
                .body(serviceWorker);
    }

    private String getThemeColor(SiteResponse site) {
        return site.getThemeColor() == null || site.getThemeColor().isBlank()
                ? SiteColor.DEFAULT_THEME_COLOR
                : site.getThemeColor();
    }

    private String getBackgroundColor(SiteResponse site) {
        return site.getBackgroundColor() == null || site.getBackgroundColor().isBlank()
                ? SiteColor.DEFAULT_BACKGROUND_COLOR
                : site.getBackgroundColor();
    }

    private String toJson(String value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("PWA 스크립트 값을 JSON 문자열로 변환할 수 없습니다.", e);
        }
    }

}
