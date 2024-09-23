package com.sparta.spartaoutsourcing.domian.store.exception;

public class UserNotFoundException extends NullPointerException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
