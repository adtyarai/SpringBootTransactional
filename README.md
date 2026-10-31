# Flight Booking Service

[![CI](https://github.com/adtyarai/SpringBootTransactional/actions/workflows/ci.yml/badge.svg)](https://github.com/adtyarai/SpringBootTransactional/actions/workflows/ci.yml)

A Spring Boot 3 REST API that books flight tickets as a single database transaction. It demonstrates Spring's declarative transaction management: when payment validation fails partway through a booking, writes that already happened are rolled back automatically, so the database never holds a passenger without a payment.

## How the transaction works

`BookingService.book()` is annotated `@Transactional` and runs three steps:

1. Saves the `Passenger`
2. Checks the payment account's credit limit via `CreditLimitService`
3. Saves the `Payment`, linked one-to-one to the passenger

If step 2 fails, it throws `InsufficientCreditException` or `AccountNotFoundException`. Both are unchecked exceptions, which Spring rolls back on by default, so the passenger written in step 1 is undone.

`BookingServiceIntegrationTest` proves this against a real database. It intentionally does **not** use `@Transactional` on the test class, because a test-managed transaction would roll back everything after each test and hide whether the service's own rollback actually worked.

## API

### `POST /api/v1/bookings`

```json
{
  "passenger": {
    "name": "Jane Doe",
    "email": "jane@example.com",
    "source": "DEL",
    "destination": "BLR",
    "travelDate": "25-12-2026",
    "pickupTime": "09:00",
    "arrivalTime": "11:30",
    "fare": 4500.00
  },
  "payment": {
    "accountNo": "acc3",
    "cardType": "CREDIT"
  }
}
```

| Outcome | Status | Body |
|---|---|---|
| Booking confirmed | `201 Created` | Booking reference, route, date and fare |
| Invalid request (missing fields, bad email, past date, non-positive fare) | `400 Bad Request` | [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) problem detail |
| Unknown payment account | `404 Not Found` | Problem detail |
| Fare exceeds credit limit | `422 Unprocessable Entity` | Problem detail; nothing is persisted |

Credit limits are held in memory by `CreditLimitService`, a stand-in for a real payment provider:

| Account | Limit |
|---|---|
| `acc1` | 12000 |
| `acc2` | 10000 |
| `acc3` | 5000 |
| `acc4` | 8000 |

## Design notes

- **Feature packages** (`booking`, `payment`) rather than layer packages, so related code lives together.
- **Request/response records are separate from JPA entities.** Clients can't set server-owned fields like IDs, and the API can change without changing the schema.
- **`BigDecimal` for money**, with explicit precision and scale on the columns, to avoid floating-point rounding errors.
- **Constructor injection** throughout; no field injection.
- **Bean Validation** on the request, so invalid input is rejected before any transaction starts.
- **Problem details** for every error response, via a single `@RestControllerAdvice`.
- **`spring.jpa.open-in-view=false`**, so lazy loading can't silently run queries outside the service layer.

## Project structure

```
src/main/java/com/aditya/flightbooking/
├── booking/   BookingController, BookingService, Passenger, request/response records
├── payment/   Payment, CreditLimitService, domain exceptions
└── common/    GlobalExceptionHandler
```

## Running locally

**Prerequisites:** Java 17+, Maven, Docker.

```bash
# Start MySQL
docker compose up -d

# Run the service (defaults match docker-compose.yml)
mvn spring-boot:run
```

Override the datasource with `DB_URL`, `DB_USERNAME` and `DB_PASSWORD` to point at another database.

Try a booking that exceeds the limit and confirm nothing is saved:

```bash
curl -i -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{"passenger":{"name":"Jane Doe","email":"jane@example.com","source":"DEL","destination":"BLR","travelDate":"25-12-2026","fare":9000},"payment":{"accountNo":"acc3","cardType":"CREDIT"}}'
```

## Tests

```bash
mvn verify
```

Tests run against an in-memory H2 database, so no MySQL is needed:

- `BookingServiceIntegrationTest`: success path, plus rollback on insufficient credit and on an unknown account
- `BookingControllerTest`: HTTP status codes and problem bodies for each outcome
- `CreditLimitServiceTest`: limit boundaries

## Tech stack

Java 17 · Spring Boot 3.3 (Web, Data JPA, Validation) · Hibernate 6 · MySQL 8 · H2 (tests) · JUnit 5 · AssertJ · Mockito · GitHub Actions
