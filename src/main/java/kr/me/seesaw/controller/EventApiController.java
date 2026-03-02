package kr.me.seesaw.controller;

import jakarta.validation.Valid;
import kr.me.seesaw.core.validation.ValidationError;
import kr.me.seesaw.dto.command.CreateEventCommand;
import kr.me.seesaw.dto.command.UpdateEventCommand;
import kr.me.seesaw.dto.model.VEventModel;
import kr.me.seesaw.dto.query.EventQuery;
import kr.me.seesaw.service.EventWebService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    @PreAuthorize("isAuthenticated() and (hasAnyRole('ADMIN', 'MANAGER') or hasPermission(#command.categoryId, 'kr.me.seesaw.domain.Category', T(kr.me.seesaw.domain.vo.BasePermission).CREATE))")
    @PostMapping
    public ResponseEntity<VEventModel> create(@Valid CreateEventCommand command) throws IOException {
        return ResponseEntity.ok(eventWebService.create(command));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<VEventModel> update(
            @PathVariable String id,
            @Valid UpdateEventCommand command
    ) throws IOException {
        return ResponseEntity.ok(eventWebService.update(id, command));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        eventWebService.delete(id);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ValidationError::new)
                .toList();
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("errors", errors));
    }

}
