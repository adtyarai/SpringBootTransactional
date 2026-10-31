package com.aditya.flightbooking.booking;

import com.aditya.flightbooking.payment.CreditLimitService;
import com.aditya.flightbooking.payment.Payment;
import com.aditya.flightbooking.payment.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final PassengerRepository passengerRepository;
    private final PaymentRepository paymentRepository;
    private final CreditLimitService creditLimitService;

    public BookingService(PassengerRepository passengerRepository,
                          PaymentRepository paymentRepository,
                          CreditLimitService creditLimitService) {
        this.passengerRepository = passengerRepository;
        this.paymentRepository = paymentRepository;
        this.creditLimitService = creditLimitService;
    }

    /**
     * Books a ticket as a single unit of work.
     * <p>
     * The passenger row is written <em>before</em> the credit check on purpose: it shows that
     * {@code @Transactional} undoes earlier writes when a later step fails. Both
     * {@code InsufficientCreditException} and {@code AccountNotFoundException} are unchecked,
     * and Spring rolls back on unchecked exceptions by default, so a failed payment never
     * leaves an orphaned passenger behind.
     */
    @Transactional
    public BookingResponse book(BookingRequest request) {
        BookingRequest.PassengerDetails details = request.passenger();
        Passenger passenger = passengerRepository.save(new Passenger(
                newBookingReference(),
                details.name(),
                details.email(),
                details.source(),
                details.destination(),
                details.travelDate(),
                details.pickupTime(),
                details.arrivalTime(),
                details.fare()));

        BookingRequest.PaymentDetails paymentDetails = request.payment();
        creditLimitService.assertSufficientCredit(paymentDetails.accountNo(), passenger.getFare());

        paymentRepository.save(new Payment(
                paymentDetails.accountNo(),
                paymentDetails.cardType(),
                passenger.getFare(),
                passenger));

        log.info("Booking {} confirmed for account {}", passenger.getBookingReference(), paymentDetails.accountNo());
        return BookingResponse.confirmed(passenger);
    }

    private static String newBookingReference() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }
}
