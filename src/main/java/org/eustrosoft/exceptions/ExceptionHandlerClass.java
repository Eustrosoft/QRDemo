package org.eustrosoft.exceptions;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.constraints.NotNull;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.file.AccessDeniedException;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionHandlerClass extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    public ExceptionHandlerClass(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String getLocalizedMessage(String messageKey) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(messageKey, null, locale);
    }

    private String getLocalizedMessage(String messageKey, Locale locale) {
        if (locale == null) {
            return getLocalizedMessage(messageKey);
        }
        return messageSource.getMessage(messageKey, null, locale);
    }

    private String getLocalizedMessage(String messageKey, Locale locale, Object... args) {
        if (locale == null) {
            return getLocalizedMessage(messageKey);
        }
        return messageSource.getMessage(messageKey, args, locale);
    }

    @ExceptionHandler({
            Throwable.class,
            Exception.class,
            ServerException.class,
            RuntimeException.class,
            UndeclaredThrowableException.class
    })
    public ResponseEntity<ExceptionObject<JsonApiError>> handleAllTypeExceptions(Exception ex, WebRequest request) {
        Locale locale = request.getLocale();

        JsonApiError error = new JsonApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                getLocalizedMessage("exceptions.title.unexpected_error", locale),
                getLocalizedMessage("exceptions.detail.unexpected_error", locale),
                ex
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new ExceptionObject<>(Collections.singletonList(error)));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status,
            @NotNull WebRequest request
    ) {
        Locale locale = request.getLocale();

        List<JsonApiError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldErr -> new JsonApiError(
                        HttpStatus.BAD_REQUEST,
                        400L,
                        getLocalizedMessage("exceptions.title.arguments_not_valid", locale),
                        fieldErr.getDefaultMessage(),
                        new JsonApiError.Source(fieldErr.getField())
                )).collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new ExceptionObject<>(errors));
    }

    @ExceptionHandler({CommonException.class})
    public ResponseEntity<ExceptionObject<JsonApiError>> handleCommonExceptions(CommonException ex, WebRequest request) {
        Locale locale = request.getLocale();
        List<JsonApiError> localizedErrors = ex.getErrors().stream()
                .map(err -> new JsonApiError(
                        err.getStatus(),
                        err.getCode(),
                        getLocalizedMessage(err.getTitle(), locale),
                        getLocalizedMessage(err.getDetail(), locale, err.getParameters()),
                        err.getSource()
                )).collect(Collectors.toList());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(new ExceptionObject<>(localizedErrors));
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ExceptionObject<JsonApiError>> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        List<JsonApiError> errors = new ArrayList<>();
        JsonApiError err = new JsonApiError(HttpStatus.FORBIDDEN, ex.getLocalizedMessage(), ex.getReason(), ex);
        errors.add(err);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(new ExceptionObject<>(errors));
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ExceptionObject<JsonApiError>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        List<JsonApiError> errors = new ArrayList<>();
        JsonApiError err = new JsonApiError(HttpStatus.FORBIDDEN, ex.getLocalizedMessage(), ex.getMessage(), ex);
        errors.add(err);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(new ExceptionObject<>(errors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflict(DataIntegrityViolationException e) {
        String message = NestedExceptionUtils.getMostSpecificCause(e).getMessage();
        JsonApiError error = new JsonApiError(
                HttpStatus.CONFLICT, 409L,
                "exceptions.title.database_violation",
                "exceptions.detail.database_violation",
                new JsonApiError.Source(message)
        );
        List<JsonApiError> errors = new ArrayList<>();
        errors.add(error);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(new ExceptionObject<>(errors));
    }

    public ResponseEntity<ExceptionObject<JsonApiError>> handleDataIntegrityExceptions(
            MethodArgumentNotValidException ex, WebRequest request
    ) {
        Locale locale = request.getLocale();

        List<JsonApiError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldErr -> new JsonApiError(
                        HttpStatus.BAD_REQUEST,
                        400L,
                        getLocalizedMessage(fieldErr.getDefaultMessage(), locale),
                        fieldErr.getDefaultMessage(),
                        new JsonApiError.Source(fieldErr.getField())
                )).collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new ExceptionObject<>(errors));
    }
}
