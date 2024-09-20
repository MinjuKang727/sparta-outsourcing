package com.sparta.spartaoutsourcing.domian.store.exception;

public class MaxStoreLimitException extends RuntimeException{
    public MaxStoreLimitException(String message) {
        super(message);
    }
}
