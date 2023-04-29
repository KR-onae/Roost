package com.kronae.roost.exception;

public class AlreadyOpenException extends RoostRuntimeException {
    public AlreadyOpenException() {
        super();
    }
    public AlreadyOpenException(String msg) {
        super(msg);
    }
}
