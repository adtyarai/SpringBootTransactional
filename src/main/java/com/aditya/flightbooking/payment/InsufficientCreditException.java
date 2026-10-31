package com.aditya.flightbooking.payment;

import java.math.BigDecimal;

public class InsufficientCreditException extends RuntimeException {

    public InsufficientCreditException(String accountNo, BigDecimal requested, BigDecimal limit) {
        super("Account " + accountNo + " has a credit limit of " + limit.toPlainString()
                + ", which does not cover " + requested.toPlainString());
    }
}
