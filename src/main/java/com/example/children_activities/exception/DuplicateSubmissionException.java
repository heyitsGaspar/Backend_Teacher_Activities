package com.example.children_activities.exception;

public class DuplicateSubmissionException extends RuntimeException {

    public DuplicateSubmissionException(String message) {
        super(message);
    }
}