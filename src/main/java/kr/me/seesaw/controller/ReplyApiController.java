package kr.me.seesaw.controller;

import jakarta.validation.Valid;
import kr.me.seesaw.command.CreateReplyCommand;
import kr.me.seesaw.command.UpdateReplyCommand;
import kr.me.seesaw.message.CmsMessageSource;
import kr.me.seesaw.model.ReplyModel;
import kr.me.seesaw.service.ArticleQueryService;
import kr.me.seesaw.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/replies")
public class ReplyApiController {

    private final CmsMessageSource messageSource;

    private final ArticleQueryService articleQueryService;

    private final ReplyService replyService;

    @GetMapping
    public ResponseEntity<Map<String, List<ReplyModel>>> getRepliesByArticleId(@RequestParam("articleId") String articleId) {
        List<ReplyModel> replies = articleQueryService.getReplies(articleId);
        return ResponseEntity.ok(Map.of("replies", replies));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> create(@Valid CreateReplyCommand command) throws IOException {
        replyService.create(command);
        String message = messageSource.getMessage("command.success.create");
        return ResponseEntity.ok(message);
    }

    @PreAuthorize("@defaultReplyService.isOwner(#id, authentication.name)")
    @PostMapping(value = "/{id}", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> update(@PathVariable("id") String id, @Valid UpdateReplyCommand command) throws IOException {
        replyService.update(id, command);
        String message = messageSource.getMessage("command.success.update");
        return ResponseEntity.ok(message);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @defaultReplyService.isOwner(#id, authentication.name)")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        replyService.delete(id);
        String message = messageSource.getMessage("command.success.delete");
        return ResponseEntity.ok(message);
    }

}
