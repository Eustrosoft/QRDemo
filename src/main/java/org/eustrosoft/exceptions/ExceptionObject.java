package org.eustrosoft.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

import java.lang.reflect.UndeclaredThrowableException;

@AllArgsConstructor
public class ExceptionObject {
    @JsonIgnore
    private final Throwable error;

    @JsonProperty("message")
    public String getMessage() {
        String message = error.getMessage();
        if (error instanceof UndeclaredThrowableException) {
            message = ((UndeclaredThrowableException) error).getUndeclaredThrowable().getMessage();
        }
        return message;
    }

    @JsonProperty("code")
    public String getCode() {
        String code = null;
        if (error instanceof CommonException) {
            code = ((CommonException) error).getCode().getCode();
        }
        return code;
    }
}
