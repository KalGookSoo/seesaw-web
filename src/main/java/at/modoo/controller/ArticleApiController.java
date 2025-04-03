package at.modoo.controller;

import at.modoo.command.CreateArticleCommand;
import at.modoo.command.UpdateArticleCommand;
import at.modoo.message.CmsMessageSource;
import at.modoo.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/articles")
public class ArticleApiController {

    private final CmsMessageSource messageSource;

    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<String> create(CreateArticleCommand command) throws IOException {
        articleService.create(command);
        String message = messageSource.getMessage("command.success.create");
        return ResponseEntity.ok(message);
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable("id") String id, UpdateArticleCommand command) throws IOException {
        articleService.update(id, command);
        String message = messageSource.getMessage("command.success.update");
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        articleService.delete(id);
        String message = messageSource.getMessage("command.success.delete");
        return ResponseEntity.ok(message);
    }

}
