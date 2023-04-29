package com.kronae.roost.exception;

public class NoConstructorException extends RoostException {
    public NoConstructorException() {
        super();
    }
    public NoConstructorException(String msg) {
        super(msg);
    }
}
