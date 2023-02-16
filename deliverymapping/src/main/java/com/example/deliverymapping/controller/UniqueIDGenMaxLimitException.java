package com.example.deliverymapping.controller;

public class UniqueIDGenMaxLimitException extends Exception {
    private static final long serialVersionUID = 1L;

    public UniqueIDGenMaxLimitException(String errorMessage) {
        super(errorMessage);
    }
}
