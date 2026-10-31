package com.aditya.transaction.Utils;

import com.aditya.transaction.Exception.AccountNotFoundException;
import com.aditya.transaction.Exception.InsufficientCreditException;

import java.util.HashMap;
import java.util.Map;

public class PaymentUtils {

    private static Map<String, Double> paymentMap = new HashMap<>();
    static {
        paymentMap.put("acc1",12000.0);
        paymentMap.put("acc2",10000.0);
        paymentMap.put("acc3",5000.0);
        paymentMap.put("acc4",8000.0);
    }

    public static boolean validateCreditLimit (String accNo, double amount) {
        Double limit = paymentMap.get(accNo);
        if (limit == null) {
            throw new AccountNotFoundException("Unknown account: " + accNo);
        }
        if (amount > limit) {
            throw new InsufficientCreditException("Insufficient credits !!!!");
        }
        return true;
    }


}
