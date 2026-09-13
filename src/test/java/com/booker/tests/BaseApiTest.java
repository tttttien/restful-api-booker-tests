package com.booker.tests;

import com.booker.models.AuthRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;

import static io.restassured.RestAssured.given;

public class BaseApiTest {

    protected static final String VALID_USERNAME = "admin";
    protected static final String VALID_PASSWORD = "password123";

    @BeforeClass
    public void setBaseUri() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    }

    protected String getAuthToken() {
        AuthRequest authRequest = new AuthRequest(VALID_USERNAME, VALID_PASSWORD);

        return given()
                .contentType(ContentType.JSON)
                .body(authRequest)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    protected Response getWithRetry(String path, int maxAttempts) {
        Response response = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            response = given()
                    .accept(ContentType.JSON)
                    .when()
                    .get(path);

            if (response.getStatusCode() != 418) {
                return response;
            }

            System.out.println("Got 418 (rate-limited) on attempt " + attempt
                    + " for GET " + path + " — retrying...");
            try {
                Thread.sleep(1500L * attempt);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return response;
    }
}
