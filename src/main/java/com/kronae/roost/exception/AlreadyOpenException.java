package com.kronae.roost.exception;

public class AlreadyOpenException extends Exception {
    public AlreadyOpenException() {
        super();
    }
    public AlreadyOpenException(String msg) {
        super(msg);
    }
}
