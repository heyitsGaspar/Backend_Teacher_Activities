package com.example.children_activities.exception;

public class ChildNotFoundException extends RuntimeException {

    public ChildNotFoundException(String message) {
        super(message);
    }
}