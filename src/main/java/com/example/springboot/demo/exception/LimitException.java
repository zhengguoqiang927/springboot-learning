package com.example.springboot.demo.exception;

public class LimitException extends RuntimeException {

    public LimitException(String msg) {
        super( msg );
    }

}
