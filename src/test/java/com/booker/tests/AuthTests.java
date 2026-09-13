package com.booker.tests;

import com.booker.models.AuthRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class AuthTests  extends BaseApiTest {

    @Test(description = "TC-API-01: Get auth token successfully with valid credentials")
    public void testLoginWithValidCredentialsReturnsToken() {
        AuthRequest validAuth = new AuthRequest(VALID_USERNAME, VALID_PASSWORD);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(validAuth)
                .when()
                .post("/auth");

        response.then().statusCode(200);

        String token = response.jsonPath().getString("token");
        Assert.assertNotNull(token, "Expected a non-null token in the response");
        Assert.assertFalse(token.isEmpty(), "Expected a non-empty token string");
    }

    @Test(description = "TC-API-02: Login fails with invalid credentials (no token returned)")
    public void testLoginWithInvalidCredentialsReturnsNoToken() {
        AuthRequest invalidAuth = new AuthRequest("wrongUser", "wrongPass");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(invalidAuth)
                .when()
                .post("/auth");

        response.then().statusCode(200);

        String token = response.jsonPath().getString("token");
        String reason = response.jsonPath().getString("reason");

        Assert.assertNull(token, "Expected no token for invalid credentials");
        Assert.assertEquals(reason, "Bad credentials",
                "Expected a 'Bad credentials' reason in the response body");
    }
}
