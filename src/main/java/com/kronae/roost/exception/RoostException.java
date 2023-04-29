package com.kronae.roost.exception;

public class RoostException extends Exception {
    public RoostException() {
        super();
    }
    public RoostException(String msg) {
        super(msg);
    }
    public RoostException(Exception msg) {
        super(msg);
    }
}
