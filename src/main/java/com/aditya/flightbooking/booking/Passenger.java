package com.aditya.flightbooking.booking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "passenger")
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 8)
    private String bookingReference;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private LocalDate travelDate;

    private String pickupTime;

    private String arrivalTime;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal fare;

    protected Passenger() {
        // required by JPA
    }

    public Passenger(String bookingReference, String name, String email, String source, String destination,
                     LocalDate travelDate, String pickupTime, String arrivalTime, BigDecimal fare) {
        this.bookingReference = bookingReference;
        this.name = name;
        this.email = email;
        this.source = source;
        this.destination = destination;
        this.travelDate = travelDate;
        this.pickupTime = pickupTime;
        this.arrivalTime = arrivalTime;
        this.fare = fare;
    }

    public Long getId() { return id; }
    public String getBookingReference() { return bookingReference; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public LocalDate getTravelDate() { return travelDate; }
    public String getPickupTime() { return pickupTime; }
    public String getArrivalTime() { return arrivalTime; }
    public BigDecimal getFare() { return fare; }
}
