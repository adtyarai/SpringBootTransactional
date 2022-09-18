package com.aditya.transaction.Exception;

public class InsufficientCreditException extends RuntimeException{

    public InsufficientCreditException (String message) {
        super(message);
    }
}
