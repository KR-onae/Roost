package com.kronae.roost.exception;

public class CannotThrowErrorException extends RoostRuntimeException {
    public CannotThrowErrorException() {
        super();
    }
    public CannotThrowErrorException(String msg) {
        super(msg);
    }
}
