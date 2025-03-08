package at.modoo.core.oas;

import at.modoo.core.file.FileIOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class OpenApiDocsWriter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RestTemplate restTemplate;

    private final String pathname;

    public OpenApiDocsWriter(String rootUri, String applicationName) {
        restTemplate = new RestTemplateBuilder().rootUri(rootUri).build();
        this.pathname = String.join(File.separator, List.of("docs", "oas", applicationName + "-api-docs.yaml"));
    }

    public void write() {
        String uri = "/v3/api-docs.yaml";
        String yaml = restTemplate.getForObject(uri, String.class);
        assert yaml != null;
        try {
            FileIOService.write(pathname, yaml.getBytes(StandardCharsets.ISO_8859_1));
        } catch (IOException e) {
            logger.error("Failed to write OpenAPI docs to file: {}", pathname, e);
        }
    }

}