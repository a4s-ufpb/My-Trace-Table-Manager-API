package com.ufpb.br.apps4society.my_trace_table_manager.api;

import com.ufpb.br.apps4society.my_trace_table_manager.api.support.TestDataSupport;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.VerificationCode;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VerificationApiTests {

    @Autowired
    private TestDataSupport testDataSupport;
    @MockBean
    private JavaMailSender mailSender;

    private static final String BASE_PATH = "/v1/verification";

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class registrationSend {

        @Test
        @DisplayName("CT-VER-001: Enviar código de cadastro para email novo")
        void CT_VER_001_sendRegistrationCodeSuccessfully_shouldReturn200() {
            String json = """
                    {
                        "email": "kawevkdev-001@gmail.com"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(json)
            .when()
                    .post(BASE_PATH + "/registration/send")
            .then()
                    .log().all()
                    .statusCode(200);
        }

        @Test
        @DisplayName("CT-VER-002: Enviar código de cadastro para email já cadastrado")
        void CT_VER_002_sendRegistrationCodeError_shouldReturn403() {
            testDataSupport.prepararUsuario("victorkawe07-002@gmail.com");
            String json = """
                    {
                        "email": "victorkawe07-002@gmail.com"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(json)
            .when()
                    .post(BASE_PATH + "/registration/send")
            .then()
                    .log().all()
                    .statusCode(400);

        }

        @Test
        @DisplayName("CT-VER-003: Enviar código de cadastros em para email inválido")
        void CT_VER_003_sendRegistrationCodeError_shouldReturn403() {
            testDataSupport.prepararUsuario("victorkawe07-003@gmail.com");
            String json = """
                    {
                        "email": ""
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(json)
            .when()
                    .post(BASE_PATH + "/registration/send")
            .then()
                    .log().all()
                    .statusCode(400);
        }
    }

    @Nested
    class recuperationSend {

        @Test
        @DisplayName("CT-VER-004: enviar código de recuperação")
        void CT_VER_004_sendResetPasswordCodeSuccessfully_shouldReturn200() {
            testDataSupport.prepararUsuario("kawevkdev-004@gmail.com");
            String json = """
                    {
                        "email": "kawevkdev-004@gmail.com"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(json)
            .when()
                    .post(BASE_PATH + "/password-reset/send")
            .then()
                    .log().all()
                    .statusCode(200);

            verify(mailSender).send(any(SimpleMailMessage.class));
        }

        @Test
        @DisplayName("CT-VER-005: email de recuperação inexistente")
        void CT_VER_005_sendResetPasswordCodeError_shouldReturn404() {
            testDataSupport.prepararUsuario("kawevkdev-005@gmail.com");
            String json = """
                    {
                        "email": "kawevk-005@gmail.com"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(json)
                    .when()
                    .post(BASE_PATH + "/password-reset/send")
                    .then()
                    .log().all()
                    .statusCode(404);
        }

        @Test
        @DisplayName("CT-VER-006: email de recuperação vazio")
        void CT_VER_006_sendResetPasswordCodeError_shouldReturn400() {
            testDataSupport.prepararUsuario("kawevkdev-006@gmail.com");
            String json = """
                    {
                        "email": ""
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(json)
            .when()
                    .post(BASE_PATH + "/password-reset/send")
            .then()
                    .log().all()
                    .statusCode(400);
        }
    }

    @Nested
    class confirmCode {
        @Test
        @DisplayName("CT-VER-007: confirmar recuperação com dados válidos")
        void CT_VER_007_confirmPasswordResetSuccessfully_shouldReturn200() {
            testDataSupport.prepararUsuario("kawevkdev-007@gmail.com");
            testDataSupport.prepararCodigo("kawevkdev-007@gmail.com", "A007AA");
            String json = """
                    {
                        "email": "kawevkdev-007@gmail.com",
                        "code": "A007AA",
                        "newPassword": "NewPassword"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(json)
            .when()
                    .post(BASE_PATH + "/password-reset/confirm")
            .then()
                    .log().all()
                    .statusCode(200);
        }

        @Test
        @DisplayName("CT-VER-008: confirmar recuperação com email vazio")
        void CT_VER_008_confirmPasswordResetError_shouldReturn400() {
            testDataSupport.prepararUsuario("kawevkdev-008@gmail.com");
            testDataSupport.prepararCodigo("kawevkdev-008@gmail.com", "A008AA");
            String json = """
                    {
                        "email": "",
                        "code": "A008AA",
                        "newPassword": "NewPassword"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(json)
            .when()
                    .post(BASE_PATH + "/password-reset/confirm")
            .then()
                    .log().all()
                    .statusCode(400);
        }

        @Test
        @DisplayName("CT-VER-009: confirmar recuperação com código vazio")
        void CT_VER_009_confirmPasswordResetError_shouldReturn400() {
            testDataSupport.prepararUsuario("kawevkdev-009@gmail.com");
            testDataSupport.prepararCodigo("kawevkdev-009@gmail.com", "A009AA");
            String json = """
                    {
                        "email": "kawevkdev-009@gmail.com",
                        "code": "",
                        "newPassword": "NewPassword"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(json)
            .when()
                    .post(BASE_PATH + "/password-reset/confirm")
            .then()
                    .log().all()
                    .statusCode(400);
        }

        @Test
        @DisplayName("CT-VER-010: confirmar recuperação com código expirado")
        void CT_VER_010_confirmPasswordResetError_shouldReturn400() {
            testDataSupport.prepararUsuario("kawevkdev-010@gmail.com");
            VerificationCode vc = testDataSupport.prepararCodigoExpirado("kawevkdev-010@gmail.com", "A010AA");

            String json = """
                    {
                        "email": "kawevkdev-010@gmail.com",
                        "code": "A010AA",
                        "newPassword": "NewPassword"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(json)
            .when()
                    .post(BASE_PATH + "/password-reset/confirm")
            .then()
                    .log().all()
                    .statusCode(400);
        }

        @Test
        @DisplayName("CT-VER-011: confirmar recuperação com código utilizado")
        void CT_VER_011_confirmPasswordResetError_shouldReturn400() {
            testDataSupport.prepararUsuario("kawevkdev-011@gmail.com");
            VerificationCode vc = testDataSupport.prepararCodigoUsado("kawevkdev-011@gmail.com", "A011AA");
            vc.setUsed(true);

            String json = """
                    {
                        "email": "kawevkdev-011@gmail.com",
                        "code": "A011AA",
                        "newPassword": "NewPassword"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(json)
            .when()
                    .post(BASE_PATH + "/password-reset/confirm")
            .then()
                    .log().all()
                    .statusCode(400);
        }

        @Test
        @DisplayName("CT-VER-012: confirmar recuperação com senha inválida")
        void CT_VER_012_confirmPasswordResetError_shouldReturn400() {
            testDataSupport.prepararUsuario("kawevkdev-012@gmail.com");
            testDataSupport.prepararCodigo("kawevkdev-012@gmail.com", "A012AA");

            String json = """
                    {
                        "email": "kawevkdev-012@gmail.com",
                        "code": "A012AA",
                        "newPassword": "NewPasswordInvalidWithMuchCharacters"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(json)
            .when()
                    .post(BASE_PATH + "/password-reset/confirm")
            .then()
                    .log().all()
                    .statusCode(400);
        }

        @Test
        @DisplayName("CT-VER-013: confirmar recuperação com código de outro usuário")
        void CT_VER_013_confirmPasswordResetError_shouldReturn400() {
            testDataSupport.prepararUsuario("kawevkdev-013@gmail.com");
            testDataSupport.prepararUsuario("victor-013@gmail.com");
            testDataSupport.prepararCodigo("kawevkdev-013@gmail.com", "A013AA");

            String json = """
                    {
                        "email": "victor-013@gmail.com",
                        "code": "A013AA",
                        "newPassword": "NewPassword"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(json)
            .when()
                    .post(BASE_PATH + "/password-reset/confirm")
            .then()
                    .log().all()
                    .statusCode(400);
        }
    }
}
