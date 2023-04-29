package com.kronae.roost.exception;

public class RoostRuntimeException extends RuntimeException {
    public RoostRuntimeException() {
        super();
    }
    public RoostRuntimeException(String msg) {
        super(msg);
    }
    public RoostRuntimeException(Exception msg) {
        super(msg);
    }
}
