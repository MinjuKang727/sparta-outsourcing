package com.sparta.spartaoutsourcing.store.exception;

public class UserNotFoundException extends NullPointerException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
