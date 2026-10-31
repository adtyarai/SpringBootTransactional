package com.aditya.flightbooking.booking;

import com.aditya.flightbooking.payment.AccountNotFoundException;
import com.aditya.flightbooking.payment.InsufficientCreditException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private static final String TRAVEL_DATE =
            LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    void returns201WithBookingOnSuccess() throws Exception {
        given(bookingService.book(any())).willReturn(new BookingResponse(
                "AB12CD34", "CONFIRMED", "Jane Doe", "DEL", "BLR",
                LocalDate.now().plusDays(30), new BigDecimal("4500.00")));

        mockMvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON).content(validBody("acc3")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingReference").value("AB12CD34"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void returns400AndSkipsServiceWhenBodyIsInvalid() throws Exception {
        String missingNameAndBadEmail = """
                {
                  "passenger": { "email": "not-an-email", "source": "DEL", "destination": "BLR",
                                 "travelDate": "%s", "fare": 4500 },
                  "payment": { "accountNo": "acc3", "cardType": "CREDIT" }
                }
                """.formatted(TRAVEL_DATE);

        mockMvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON).content(missingNameAndBadEmail))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    void returns422ProblemWhenCreditIsInsufficient() throws Exception {
        given(bookingService.book(any())).willThrow(
                new InsufficientCreditException("acc3", new BigDecimal("9000"), new BigDecimal("5000")));

        mockMvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON).content(validBody("acc3")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Insufficient credit"));
    }

    @Test
    void returns404ProblemWhenAccountIsUnknown() throws Exception {
        given(bookingService.book(any())).willThrow(new AccountNotFoundException("nope"));

        mockMvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON).content(validBody("nope")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Account not found"));
    }

    private static String validBody(String accountNo) {
        return """
                {
                  "passenger": { "name": "Jane Doe", "email": "jane@example.com", "source": "DEL",
                                 "destination": "BLR", "travelDate": "%s", "pickupTime": "09:00",
                                 "arrivalTime": "11:30", "fare": 4500 },
                  "payment": { "accountNo": "%s", "cardType": "CREDIT" }
                }
                """.formatted(TRAVEL_DATE, accountNo);
    }
}
