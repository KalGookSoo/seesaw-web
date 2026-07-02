package kr.me.seesaw.web;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import kr.me.seesaw.core.support.message.CmsMessageSource;
import kr.me.seesaw.core.support.validation.ValidationError;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 전역 예외 핸들러
 */
@Hidden
@RequiredArgsConstructor
@ControllerAdvice
public class SeesawWebControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CmsMessageSource messageSource;

    @ExceptionHandler(value = NoSuchElementException.class, produces = MediaType.TEXT_HTML_VALUE)
    public String handleNoSuchElementExceptionHtml(NoSuchElementException e) {
        logger.error(e.getMessage());
        return "error/404";
    }

    @ExceptionHandler(value = NoSuchElementException.class, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> handleNoSuchElementExceptionJson(NoSuchElementException e) {
        logger.error(e.getMessage());
        String message = messageSource.getMessage("error.not.found.resource");
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(Map.of("message", message));
    }

    @ExceptionHandler(value = EntityNotFoundException.class, produces = MediaType.TEXT_HTML_VALUE)
    public String handleEntityNotFoundExceptionHtml(EntityNotFoundException ex) {
        return handleNoSuchElementExceptionHtml(new NoSuchElementException(ex));
    }

    @ExceptionHandler(value = EntityNotFoundException.class, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundExceptionJson(EntityNotFoundException ex) {
        return handleNoSuchElementExceptionJson(new NoSuchElementException(ex));
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class, produces = MediaType.TEXT_HTML_VALUE)
    public String handleDataIntegrityViolationExceptionHtml(DataIntegrityViolationException e) {
        logger.error(e.getMessage());
        return "error/400";
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationExceptionJson(DataIntegrityViolationException e) {
        logger.error(e.getMessage());
        String message = messageSource.getMessage("error.constraint.violation");
        return ResponseEntity.badRequest()
                .body(Map.of("message", message));
    }

    @ExceptionHandler(value = ConstraintViolationException.class, produces = MediaType.TEXT_HTML_VALUE)
    public String handleConstraintViolationExceptionHtml(ConstraintViolationException ex) {
        logger.error(ex.getMessage());
        return "error/400";
    }

    @ExceptionHandler(value = ConstraintViolationException.class, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, List<Map<String, String>>>> handleConstraintViolationExceptionJson(ConstraintViolationException ex) {
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("errors", constraintViolationErrors(ex)));
    }

    @ExceptionHandler(value = MaxUploadSizeExceededException.class, produces = MediaType.TEXT_HTML_VALUE)
    public String handleMaxUploadSizeExceededExceptionHtml(MaxUploadSizeExceededException ex) {
        logger.error(ex.getMessage());
        return "error/400";
    }

    @ExceptionHandler(value = MaxUploadSizeExceededException.class, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> handleMaxUploadSizeExceededExceptionJson(MaxUploadSizeExceededException ex) {
        logger.error(ex.getMessage());
        String message = messageSource.getMessage("error.payload.too.large");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(Map.of("message", message));
    }

    @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class, produces = MediaType.TEXT_HTML_VALUE)
    public String handleHttpMediaTypeNotSupportedHtml(HttpMediaTypeNotSupportedException ex) {
        logger.error(ex.getMessage());
        return "error/400";
    }

    @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> handleHttpMediaTypeNotSupportedJson(HttpMediaTypeNotSupportedException ex) {
        logger.error(ex.getMessage());
        String message = messageSource.getMessage("error.unsupported.media.type");
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(Map.of("message", message));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class, produces = MediaType.TEXT_HTML_VALUE)
    public String handleMethodArgumentNotValidHtml(MethodArgumentNotValidException ex) {
        logger.error(ex.getMessage());
        return "error/400";
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, List<ValidationError>>> handleMethodArgumentNotValidJson(MethodArgumentNotValidException ex) {
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("errors", validationErrors(ex)));
    }

    @ExceptionHandler(value = BindException.class, produces = MediaType.TEXT_HTML_VALUE)
    public String handleBindExceptionHtml(BindException ex) {
        logger.error(ex.getMessage());
        return "error/400";
    }

    @ExceptionHandler(value = BindException.class, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, List<ValidationError>>> handleBindExceptionJson(BindException ex) {
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("errors", validationErrors(ex)));
    }

    @ExceptionHandler(value = AccessDeniedException.class, produces = MediaType.TEXT_HTML_VALUE)
    public String handleAccessDeniedExceptionHtml(AccessDeniedException e) {
        logger.error(e.getMessage());
        return "error/403";
    }

    @ExceptionHandler(value = AccessDeniedException.class, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> handleAccessDeniedExceptionJson(AccessDeniedException e) {
        logger.error(e.getMessage());
        String message = messageSource.getMessage("error.access.denied");
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", message));
    }

    @ExceptionHandler(value = InvalidCsrfTokenException.class, produces = MediaType.TEXT_HTML_VALUE)
    public String handleInvalidCsrfTokenExceptionHtml(InvalidCsrfTokenException e) {
        logger.error(e.getMessage());
        return "error/403";
    }

    @ExceptionHandler(value = InvalidCsrfTokenException.class, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> handleInvalidCsrfTokenExceptionJson(InvalidCsrfTokenException e) {
        logger.error(e.getMessage());
        return handleAccessDeniedExceptionJson(e);
    }

    private List<ValidationError> validationErrors(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ValidationError::new)
                .toList();
    }

    private List<ValidationError> validationErrors(BindException ex) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ValidationError::new)
                .toList();
    }

    private List<Map<String, String>> constraintViolationErrors(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                .stream()
                .map(violation -> Map.of(
                        "field", violation.getPropertyPath().toString(),
                        "message", violation.getMessage(),
                        "rejectedValue", String.valueOf(violation.getInvalidValue())
                ))
                .toList();
    }

}
