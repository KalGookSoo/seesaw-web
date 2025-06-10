package at.modoo.controller;

import at.modoo.command.CreateArticleCommand;
import at.modoo.command.UpdateArticleCommand;
import at.modoo.message.CmsMessageSource;
import at.modoo.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/articles")
public class ArticleApiController {

    private final CmsMessageSource messageSource;

    private final ArticleService articleService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<String> create(@Valid CreateArticleCommand command) throws IOException {
        articleService.create(command);
        String message = messageSource.getMessage("command.success.create");
        return ResponseEntity.ok(message);
    }

    @PreAuthorize("@defaultArticleService.isOwner(#id, authentication.name)")
    @PostMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable("id") String id, @Valid UpdateArticleCommand command) throws IOException {
        articleService.update(id, command);
        String message = messageSource.getMessage("command.success.update");
        return ResponseEntity.ok(message);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @defaultArticleService.isOwner(#id, authentication.name)")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        articleService.delete(id);
        String message = messageSource.getMessage("command.success.delete");
        return ResponseEntity.ok(message);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping
    public ResponseEntity<String> deleteAll(@RequestBody List<String> ids) {
        articleService.deleteAll(ids);
        String message = messageSource.getMessage("command.success.delete");
        return ResponseEntity.ok(message);
    }

}
