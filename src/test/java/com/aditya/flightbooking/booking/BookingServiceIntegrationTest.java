package com.aditya.flightbooking.booking;

import com.aditya.flightbooking.payment.AccountNotFoundException;
import com.aditya.flightbooking.payment.InsufficientCreditException;
import com.aditya.flightbooking.payment.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Runs against a real (in-memory) database through the full Spring transaction proxy.
 * <p>
 * Deliberately <strong>not</strong> annotated with {@code @Transactional}: a test-managed
 * transaction would wrap the service call and roll everything back at the end of each test,
 * which would hide whether the service's own rollback actually happened.
 */
@SpringBootTest
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void cleanDatabase() {
        paymentRepository.deleteAll();
        passengerRepository.deleteAll();
    }

    @Test
    void persistsPassengerAndPaymentWhenCreditIsSufficient() {
        BookingResponse response = bookingService.book(request("acc3", "4500.00"));

        assertThat(response.status()).isEqualTo("CONFIRMED");
        assertThat(response.bookingReference()).hasSize(8);
        assertThat(response.totalFare()).isEqualByComparingTo("4500.00");
        assertThat(passengerRepository.count()).isEqualTo(1);
        assertThat(paymentRepository.count()).isEqualTo(1);
    }

    @Test
    void rollsBackPassengerWhenCreditIsInsufficient() {
        assertThatThrownBy(() -> bookingService.book(request("acc3", "9000.00")))
                .isInstanceOf(InsufficientCreditException.class);

        // The passenger was saved before the credit check failed; the rollback must remove it.
        assertThat(passengerRepository.count()).isZero();
        assertThat(paymentRepository.count()).isZero();
    }

    @Test
    void rollsBackPassengerWhenAccountIsUnknown() {
        assertThatThrownBy(() -> bookingService.book(request("does-not-exist", "100.00")))
                .isInstanceOf(AccountNotFoundException.class);

        assertThat(passengerRepository.count()).isZero();
        assertThat(paymentRepository.count()).isZero();
    }

    private static BookingRequest request(String accountNo, String fare) {
        return new BookingRequest(
                new BookingRequest.PassengerDetails(
                        "Jane Doe", "jane@example.com", "DEL", "BLR",
                        LocalDate.now().plusDays(30), "09:00", "11:30", new BigDecimal(fare)),
                new BookingRequest.PaymentDetails(accountNo, "CREDIT"));
    }
}
