# Restful-Booker API Testing Project

RestAssured + Java + TestNG automation project for
[https://restful-booker.herokuapp.com](https://restful-booker.herokuapp.com),
a public hotel-booking demo API. A Postman collection covering the same test
cases is also included.

## 1. Project structure

```
restful-booker-api-tests/
├── pom.xml
├── testng.xml
├── postman/
│   └── Restful-Booker-API-Tests.postman_collection.json
└── src/test/java/com/booker/
    ├── models/
    │   ├── Booking.java
    │   ├── BookingDates.java
    │   └── AuthRequest.java
    └── tests/
        ├── BaseApiTest.java       
        ├── AuthTests.java          (TC-API-01, TC-API-02)
        ├── BookingCrudTests.java   (TC-API-03, 04, 05, 07)
        └── NegativeTests.java      (TC-API-06, TC-API-08)
```

## 2. Tech Stack

- Language: Java
- API Testing Library: RestAssured
- Test Framework: TestNG
- Build Tool: Maven
- JSON Mapping: Jackson (POJO-based request/response bodies)
- API Testing (manual/exploratory): Postman
- Version Control: Git / GitHub

## 3. Test Scope

The project focuses on testing the core functionality of the Restful-Booker API:

- Authentication (token generation)
- Booking creation, retrieval, update, and deletion (full CRUD)
- Authorization checks on protected endpoints
- Input validation on required fields

## 4. Test Cases

| TC ID | Module | Test Case | Precondition | Test Steps | Test Data | Expected Result |
|---|---|---|---|---|---|---|
| TC-API-01 | Auth | Get auth token with valid credentials | N/A | 1. Send POST /auth with valid username/password | username: admin, password: password123 | Response status 200 and a non-empty token is returned |
| TC-API-02 | Auth | Login fails with invalid credentials | N/A | 1. Send POST /auth with invalid username/password | username: wrongUser, password: wrongPass | Response status 200 with no token; body contains reason: "Bad credentials" |
| TC-API-03 | Booking CRUD | Create a new booking | N/A | 1. Send POST /booking with valid booking data | firstname, lastname, totalprice, dates | Response status 200; a valid bookingid is returned and echoed data matches input |
| TC-API-04 | Booking CRUD | Retrieve booking by ID | A booking was created in TC-API-03 | 1. Send GET /booking/{id} | bookingId from TC-API-03 | Response status 200; returned data matches the created booking |
| TC-API-05 | Booking CRUD | Update booking with valid token | Booking exists; valid token obtained | 1. Send PUT /booking/{id} with token and updated data | Updated lastname, totalprice | Response status 200; response reflects updated fields |
| TC-API-06 | Authorization | Update booking without token | Booking exists | 1. Send PUT /booking/{id} without a token | N/A | Response status 403 Forbidden |
| TC-API-07 | Booking CRUD | Delete booking and verify removal | Booking exists; valid token obtained | 1. Send DELETE /booking/{id} with token<br>2. Send GET /booking/{id} again | N/A | DELETE returns 201; subsequent GET returns 404 |
| TC-API-08 | Validation | Create booking with missing required field | N/A | 1. Send POST /booking omitting "firstname" | lastname, totalprice, dates (no firstname) | Response should reject the request with 400 Bad Request |

## 5. Test Execution

### Prerequisites

- Java JDK 11+
- Maven
- Git
- (Optional) Postman, to run the included collection

### Run all tests

```bash
mvn clean test
```

### Test Execution Summary

| Module | Test Cases | Passed | Failed | Errors | Skipped |
|---|---|---|---|---|---|
| Auth | 2 | 2 | 0 | 0 | 0 |
| Booking CRUD | 4 | 1 | 1 | 0 | 2 |
| Authorization / Validation | 2 | 2 | 0 | 0 | 0 |
| **Total** | **8** | **5** | **1** | **0** | **2** |

*Note: TC-API-05 and TC-API-07 are chained after TC-API-04 (`dependsOnMethods`)
and are automatically skipped by TestNG when TC-API-04 fails, since they need
its result to run.*

## 6. Defects

| ID     | Module | Scenario | Expected Result | Actual Result | Status |
|--------|---|---|---|---|--------|
| BUG-01 | Booking CRUD (Infra) | TC-API-04: GET /booking/{id} | API should reliably return 200 with the correct booking | API intermittently returns 418 ("I'm a teapot"), even after retrying 3 times with backoff. Consistent with rate-limiting/anti-bot behavior on the public demo host under load | Failed |

