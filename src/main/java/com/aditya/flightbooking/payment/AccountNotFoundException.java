package com.aditya.flightbooking.payment;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String accountNo) {
        super("Unknown payment account: " + accountNo);
    }
}
