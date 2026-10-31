package com.aditya.flightbooking.payment;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Stand-in for an external payment provider. Limits are held in memory so the
 * project runs without third-party accounts; swapping in a real gateway only
 * requires replacing this bean.
 */
@Service
public class CreditLimitService {

    private final Map<String, BigDecimal> creditLimits = Map.of(
            "acc1", new BigDecimal("12000.00"),
            "acc2", new BigDecimal("10000.00"),
            "acc3", new BigDecimal("5000.00"),
            "acc4", new BigDecimal("8000.00"));

    /**
     * @throws AccountNotFoundException    if the account is unknown
     * @throws InsufficientCreditException if {@code amount} exceeds the account's limit
     */
    public void assertSufficientCredit(String accountNo, BigDecimal amount) {
        BigDecimal limit = creditLimits.get(accountNo);
        if (limit == null) {
            throw new AccountNotFoundException(accountNo);
        }
        if (amount.compareTo(limit) > 0) {
            throw new InsufficientCreditException(accountNo, amount, limit);
        }
    }
}
