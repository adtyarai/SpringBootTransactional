package com.aditya.transaction.Controller;

import com.aditya.transaction.DTO.FlightBookingAck;
import com.aditya.transaction.DTO.FlightBookingRequest;
import com.aditya.transaction.Service.FlightBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableTransactionManagement
public class FlightBookingController {

    @Autowired
    private FlightBookingService flightBookingService;

    @PostMapping("/book")
    public FlightBookingAck bookFlightTicket (@RequestBody FlightBookingRequest flightBookingRequest) {
        FlightBookingAck flightBookingAck = flightBookingService.bookFlightTicket(flightBookingRequest);
        return flightBookingAck;
    }
}
