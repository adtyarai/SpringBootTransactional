package com.aditya.transaction.DTO;

import com.aditya.transaction.Entity.PassengerInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightBookingAck {

    private String status;
    private double totalFare;
    private String pnrNO;
    private PassengerInfo passengerInfo;
}
