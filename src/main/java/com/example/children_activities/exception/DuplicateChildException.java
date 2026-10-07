package com.example.children_activities.exception;

public class DuplicateChildException extends RuntimeException {
    public DuplicateChildException(String message) {
        super(message);
    }
}