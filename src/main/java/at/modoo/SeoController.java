package at.modoo;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Controller
public class SeoController {

    @GetMapping("/sitemap.xml")
    @ResponseBody
    public ResponseEntity<String> getSitemap() throws IOException {
        Resource resource = new ClassPathResource("static/sitemap.xml");
        String content = Files.readString(resource.getFile().toPath());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(content);
    }

}