package com.booker.tests;

import com.booker.models.Booking;
import com.booker.models.BookingDates;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class BookingCrudTests extends BaseApiTest{
    private int bookingId;

    @Test(description = "TC-API-03: Create a new booking successfully")
    public void testCreateBooking() {
        Booking newBooking = new Booking(
                "Tien",
                "Nguyen",
                150,
                true,
                new BookingDates("2026-01-01", "2026-01-10"),
                "Late checkout"
        );

        Response response = given()
                .contentType(ContentType.JSON)
                .body(newBooking)
                .when()
                .post("/booking");

        response.then().statusCode(200);

        bookingId = response.jsonPath().getInt("bookingid");
        Assert.assertTrue(bookingId > 0, "Expected a valid (positive) booking ID to be returned");

        String firstname = response.jsonPath().getString("booking.firstname");
        Assert.assertEquals(firstname, "Tien",
                "Expected the created booking to echo back the firstname we sent");
    }

    @Test(description = "TC-API-04: Retrieve the created booking by ID",
            dependsOnMethods = "testCreateBooking")
    public void testGetBookingById() {
        Response response = getWithRetry("/booking/" + bookingId, 3);

        response.then().statusCode(200);

        Assert.assertEquals(response.jsonPath().getString("firstname"), "Tien");
        Assert.assertEquals(response.jsonPath().getString("lastname"), "Nguyen");
        Assert.assertEquals(response.jsonPath().getInt("totalprice"), 150);
    }

    @Test(description = "TC-API-05: Update the booking with a valid auth token",
            dependsOnMethods = "testGetBookingById")
    public void testUpdateBookingWithValidToken() {
        String token = getAuthToken();

        Booking updatedBooking = new Booking(
                "Nguyenn",
                "Updated",
                200,
                false,
                new BookingDates("2026-02-01", "2026-02-15"),
                "Early check-in"
        );

        Response response = given()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(updatedBooking)
                .when()
                .put("/booking/" + bookingId);

        response.then().statusCode(200);

        Assert.assertEquals(response.jsonPath().getString("lastname"), "Updated");
        Assert.assertEquals(response.jsonPath().getInt("totalprice"), 200);
    }

    @Test(description = "TC-API-07: Delete the booking, then verify GET returns 404",
            dependsOnMethods = "testUpdateBookingWithValidToken")
    public void testDeleteBookingThenVerifyGone() {
        String token = getAuthToken();

        given()
                .cookie("token", token)
                .when()
                .delete("/booking/" + bookingId)
                .then()
                .statusCode(201); // Restful-Booker returns 201 on successful delete

        // Verify the booking really is gone
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/booking/" + bookingId)
                .then()
                .statusCode(404);
    }
}
