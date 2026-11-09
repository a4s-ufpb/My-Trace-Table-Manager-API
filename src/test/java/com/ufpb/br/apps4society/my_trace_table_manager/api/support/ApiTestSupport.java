package com.ufpb.br.apps4society.my_trace_table_manager.api.support;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

public class ApiTestSupport {

    private static final String ADMIN_EMAIL = "admin@test.com";
    private static final String ADMIN_PASSWORD = "password";

    private ApiTestSupport() {
    }

    public static String obterTokenAdmin() {
        return obterToken(ADMIN_EMAIL, ADMIN_PASSWORD);
    }

    public static String obterToken(String email, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "email": "%s",
                            "password": "%s"
                        }
                        """.formatted(email, password))
                .when()
                .post("/v1/user/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}
