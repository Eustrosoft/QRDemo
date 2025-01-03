package org.eustrosoft.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.reflect.UndeclaredThrowableException;
import java.nio.file.AccessDeniedException;
import java.rmi.ServerException;

@ControllerAdvice
public class ExceptionHandlerClass extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            Throwable.class,
            Exception.class,
            ServerException.class,
            RuntimeException.class,
            UndeclaredThrowableException.class,
            AccessDeniedException.class
    })
    public ResponseEntity<Object> handleAllTypeExceptions(Exception ex, WebRequest request) {
        ex.printStackTrace();
        logger.error(ex.getLocalizedMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionObject(ex));
    }
}
