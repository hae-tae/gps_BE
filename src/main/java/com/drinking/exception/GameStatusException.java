package com.drinking.exception;

public class GameStatusException extends RuntimeException {
    public GameStatusException(String message) {
        super(message);
    }
}