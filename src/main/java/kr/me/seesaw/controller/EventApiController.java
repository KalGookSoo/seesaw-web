package kr.me.seesaw.controller;

import jakarta.validation.Valid;
import kr.me.seesaw.dto.command.CreateEventCommand;
import kr.me.seesaw.dto.command.UpdateEventCommand;
import kr.me.seesaw.dto.model.VEventModel;
import kr.me.seesaw.dto.query.EventQuery;
import kr.me.seesaw.service.EventWebService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventApiController {

    private final EventWebService eventWebService;

    @GetMapping
    public ResponseEntity<List<VEventModel>> findAll(EventQuery eventQuery) {
        return ResponseEntity.ok(eventWebService.findAll(eventQuery));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VEventModel> find(@PathVariable String id) {
        return ResponseEntity.ok(eventWebService.find(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<VEventModel> create(
            @Valid @RequestPart("command") CreateEventCommand command,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {
        return ResponseEntity.ok(eventWebService.create(command, files));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<VEventModel> update(
            @PathVariable String id,
            @Valid @RequestPart("command") UpdateEventCommand command,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {
        return ResponseEntity.ok(eventWebService.update(id, command, files));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        eventWebService.delete(id);
        return ResponseEntity.ok().build();
    }

}
