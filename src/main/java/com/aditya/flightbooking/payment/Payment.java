package com.aditya.flightbooking.payment;

import com.aditya.flightbooking.booking.Passenger;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String accountNo;

    @Column(nullable = false)
    private String cardType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passenger_id", nullable = false, unique = true)
    private Passenger passenger;

    protected Payment() {
        // required by JPA
    }

    public Payment(String accountNo, String cardType, BigDecimal amount, Passenger passenger) {
        this.accountNo = accountNo;
        this.cardType = cardType;
        this.amount = amount;
        this.passenger = passenger;
    }

    public UUID getId() { return id; }
    public String getAccountNo() { return accountNo; }
    public String getCardType() { return cardType; }
    public BigDecimal getAmount() { return amount; }
    public Passenger getPassenger() { return passenger; }
}
