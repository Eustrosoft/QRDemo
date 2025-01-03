package org.eustrosoft.exceptions;

import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {

    public Code code;

    public CommonException(Code code) {
        this.code = code;
    }

    public CommonException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public CommonException(Code code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
    }
}
