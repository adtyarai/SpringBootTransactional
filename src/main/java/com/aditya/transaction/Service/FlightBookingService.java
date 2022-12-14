package com.aditya.transaction.Service;

import com.aditya.transaction.DTO.FlightBookingAck;
import com.aditya.transaction.DTO.FlightBookingRequest;
import com.aditya.transaction.Entity.PassengerInfo;
import com.aditya.transaction.Entity.PaymentInfo;
import com.aditya.transaction.Repository.PassengerInfoRepository;
import com.aditya.transaction.Repository.PaymentInfoRepository;
import com.aditya.transaction.Utils.PaymentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FlightBookingService {

    @Autowired
    private PassengerInfoRepository passengerInfoRepository;

    @Autowired
    private PaymentInfoRepository paymentInfoRepository;

    @Transactional
            //(readOnly = false, isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public FlightBookingAck bookFlightTicket (FlightBookingRequest flightBookingRequest) {
        PassengerInfo passengerInfo = flightBookingRequest.getPassengerInfo();
        passengerInfo = passengerInfoRepository.save(passengerInfo);

        PaymentInfo paymentInfo = flightBookingRequest.getPaymentInfo();
        Boolean checkIfCreditIsEnough = PaymentUtils.validateCreditLimit(paymentInfo.getAccountNo(), passengerInfo.getFare());
        if(checkIfCreditIsEnough) {
            paymentInfo.setPassengerId(passengerInfo.getPId());
            paymentInfo.setAmount(passengerInfo.getFare());
            paymentInfoRepository.save(paymentInfo);

            return new FlightBookingAck("SUCCESS",
                    passengerInfo.getFare(),
                    UUID.randomUUID().toString().split("-")[0],
                    passengerInfo);
        }
        return new FlightBookingAck("Insufficient credits !!!!",
                passengerInfo.getFare(),
                UUID.randomUUID().toString().split("-")[0],
                passengerInfo);
    }
}
