package com.aditya.transaction.Utils;

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
        if(amount > paymentMap.get(accNo)) {
            throw new InsufficientCreditException("Insufficient credits !!!!");
        } else return true;
    }


}
