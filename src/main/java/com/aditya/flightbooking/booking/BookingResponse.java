package com.aditya.flightbooking.booking;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingResponse(
        String bookingReference,
        String status,
        String passengerName,
        String source,
        String destination,
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate travelDate,
        BigDecimal totalFare) {

    static BookingResponse confirmed(Passenger passenger) {
        return new BookingResponse(
                passenger.getBookingReference(),
                "CONFIRMED",
                passenger.getName(),
                passenger.getSource(),
                passenger.getDestination(),
                passenger.getTravelDate(),
                passenger.getFare());
    }
}
