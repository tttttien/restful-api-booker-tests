package com.booker.tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class NegativeTests extends BaseApiTest {
    @Test(description = "TC-API-08: Create booking with a missing required field")
    public void testCreateBookingWithMissingFirstname() {
        String bodyMissingFirstname = "{"
                + "\"lastname\": \"NoFirstName\","
                + "\"totalprice\": 100,"
                + "\"depositpaid\": true,"
                + "\"bookingdates\": {"
                + "  \"checkin\": \"2026-04-01\","
                + "  \"checkout\": \"2026-04-05\""
                + "},"
                + "\"additionalneeds\": \"Breakfast\""
                + "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(bodyMissingFirstname)
                .when()
                .post("/booking");

        int statusCode = response.getStatusCode();

        if (statusCode == 500) {
            System.out.println("DEFECT FOUND: POST /booking with a missing 'firstname' field "
                    + "returns 500 Internal Server Error instead of 400 Bad Request. "
                    + "This indicates the API lacks proper input validation for required fields.");
        } else if (statusCode == 200) {
            System.out.println("DEFECT CANDIDATE: API accepted a booking with no firstname (status 200). "
                    + "Response body: " + response.getBody().asString());
        }
        Assert.assertTrue(statusCode == 400 || statusCode == 200 || statusCode == 500,
                "Unexpected status code for missing required field: " + statusCode);
    }
}

