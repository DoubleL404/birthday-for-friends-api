package com.doublel401.java.bff.exception;

import lombok.Getter;

@Getter
public class IllegalArgumentException extends RuntimeException{
    public IllegalArgumentException(String message) {
        super(message);
    }

    public IllegalArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
