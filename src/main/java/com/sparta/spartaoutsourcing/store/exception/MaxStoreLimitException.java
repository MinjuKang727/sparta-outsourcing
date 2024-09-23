package com.sparta.spartaoutsourcing.store.exception;

public class MaxStoreLimitException extends RuntimeException{
    public MaxStoreLimitException(String message) {
        super(message);
    }
}
