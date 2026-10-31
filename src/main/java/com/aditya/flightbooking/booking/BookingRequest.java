package com.aditya.flightbooking.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Inbound booking payload. Kept separate from the JPA entities so the API contract
 * can evolve independently of the persistence model, and so clients cannot set
 * server-owned fields such as IDs.
 */
public record BookingRequest(
        @NotNull @Valid PassengerDetails passenger,
        @NotNull @Valid PaymentDetails payment) {

    public record PassengerDetails(
            @NotBlank String name,
            @NotBlank @Email String email,
            @NotBlank String source,
            @NotBlank String destination,
            @NotNull @FutureOrPresent @JsonFormat(pattern = "dd-MM-yyyy") LocalDate travelDate,
            String pickupTime,
            String arrivalTime,
            @NotNull @Positive BigDecimal fare) {
    }

    public record PaymentDetails(
            @NotBlank String accountNo,
            @NotBlank String cardType) {
    }
}
