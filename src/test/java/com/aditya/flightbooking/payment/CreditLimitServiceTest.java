package com.aditya.flightbooking.payment;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreditLimitServiceTest {

    private final CreditLimitService service = new CreditLimitService();

    @Test
    void allowsAmountEqualToLimit() {
        assertThatCode(() -> service.assertSufficientCredit("acc3", new BigDecimal("5000.00")))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsAmountAboveLimit() {
        assertThatThrownBy(() -> service.assertSufficientCredit("acc3", new BigDecimal("5000.01")))
                .isInstanceOf(InsufficientCreditException.class);
    }

    @Test
    void rejectsUnknownAccount() {
        assertThatThrownBy(() -> service.assertSufficientCredit("missing", BigDecimal.ONE))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("missing");
    }
}
