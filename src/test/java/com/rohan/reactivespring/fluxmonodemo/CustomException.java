package com.rohan.reactivespring.fluxmonodemo;

public class CustomException extends Throwable {

    private final String message;

    public CustomException(Throwable e) {
        this.message = e.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
