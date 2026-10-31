# SpringBootTransactional

A Spring Boot service demonstrating declarative transaction management with `@Transactional` — specifically, how a runtime exception mid-operation rolls back database writes that already happened earlier in the same transaction.

The scenario is a flight booking: passenger details are saved first, then the payment is validated against a credit limit. If the credit check fails, the passenger row is rolled back too, so the database never ends up with a passenger who has no payment.

## Tech stack

- Java 17
- Spring Boot 2.7.3 (Spring Web, Spring Data JPA)
- Hibernate
- MySQL
- Lombok
- Maven

## How the transaction works

`FlightBookingService.bookFlightTicket()` is annotated `@Transactional` and does two writes in sequence:

1. Saves `PassengerInfo` via `PassengerInfoRepository`
2. Validates the payment account's credit limit through `PaymentUtils.validateCreditLimit()`
3. Saves `PaymentInfo` if the credit check passes

When the account has insufficient credit, step 2 throws `InsufficientCreditException` — an unchecked exception, which is what Spring rolls back on by default. The passenger row written in step 1 is rolled back with it.

Credit limits are held in an in-memory map in `PaymentUtils` (no external payment system):

| Account | Limit |
|---------|-------|
| `acc1`  | 12000 |
| `acc2`  | 10000 |
| `acc3`  | 5000  |
| `acc4`  | 8000  |

## API

`POST /book`

```json
{
  "passengerInfo": {
    "name": "Jane Doe",
    "email": "jane@example.com",
    "source": "DEL",
    "destination": "BLR",
    "travelDate": "25-12-2024",
    "pickupTime": "09:00",
    "arrivalTime": "11:30",
    "fare": 4500
  },
  "paymentInfo": {
    "accountNo": "acc3",
    "cardType": "CREDIT"
  }
}
```

Returns a `FlightBookingAck` with a status, the fare, a booking reference, and the saved passenger details.

To see the rollback, send a `fare` higher than the account's limit (e.g. `fare: 9000` against `acc3`) and confirm no row lands in `Passenger_Info`.

## Project structure

```
Controller/  FlightBookingController — POST /book
Service/     FlightBookingService — the @Transactional unit of work
Repository/  PassengerInfoRepository, PaymentInfoRepository
Entity/      PassengerInfo, PaymentInfo
DTO/         FlightBookingRequest, FlightBookingAck
Exception/   InsufficientCreditException
Utils/       PaymentUtils — in-memory credit limits
```

## Running locally

**Prerequisites:** Java 17, Maven, and a MySQL instance.

1. Create the database:
   ```sql
   CREATE DATABASE springBootTransaction;
   ```
2. Set your datasource in `src/main/resources/application.properties` (or override via environment variables):
   ```
   spring.datasource.url=jdbc:mysql://localhost:3306/springBootTransaction
   spring.datasource.username=<your-user>
   spring.datasource.password=<your-password>
   ```
   `spring.jpa.hibernate.ddl-auto=update` creates the tables on first run.
3. Run:
   ```
   mvn spring-boot:run
   ```

## Notes

A learning project exploring Spring's transaction semantics — rollback behaviour, isolation levels, and propagation. Not a production booking service: there's no authentication, no real payment gateway, and no integration tests.
